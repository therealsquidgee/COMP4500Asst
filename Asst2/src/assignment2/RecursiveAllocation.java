package assignment2;

import java.util.*;

public class RecursiveAllocation {

	/**
	 * @param numWorkers
	 *            The number of workers that are available to work. You may
	 *            assume that numWorkers >= 0.
	 * @param maxHours
	 *            The maximum number of whole hours that each worker can work
	 *            for the week. You may assume that maxHours >= 0.
	 * @param jobs
	 *            A list describing the list of jobs to allocate, in order of
	 *            priority. Each job is described by the number of whole hours
	 *            that it will take to complete. That is, there are jobs.size()
	 *            jobs on the list, and the ith highest priority job will take
	 *            jobs.get(i) whole hours to complete. The number of hours that
	 *            any job will take to complete is greater than 0.
	 * 
	 * @return Given the parameters of the allocation problem, this method
	 *         returns the maximum performance-metric rating achievable by the
	 *         manager, given that the intern seeks to make job allocations that
	 *         minimise the rating. (See handout for details.)
	 * 
	 *         This method must be implemented using a recursive programming
	 *         solution to the problem. It is expected to have a worst-case
	 *         exponential running time.
	 */
	public static int maxRatingRecursive(int numWorkers, int maxHours,
			List<Integer> jobs) {
		
		Integer[] freeTime = new Integer[numWorkers];
		for(int i = 0; i < numWorkers; i++){
			freeTime[i] = maxHours;
		}
		
		int currentMaxRating = internAllocate(jobs, freeTime)[0];
		
		//System.out.println(getMaxRatingRecursive(numWorkers, jobs, freeTime, 0, 
			//	Integer.MIN_VALUE));
		
		return getMaxRatingRecursive(numWorkers, jobs.subList(1, jobs.size()), 
				freeTime, 0, currentMaxRating);
	}

	private static int getMaxRatingRecursive(int numWorkers, List<Integer> jobs, 
			Integer[] freeTime, int currentJobPriority, int currentMaxRating) {
		
		// Base case
		if(jobs.size() == 0){
			return currentMaxRating;
		}
		// Write a function for when jobs.size() < 3
		// 
		else {
			Integer[] resultOfAllocs = allocatePastIntern(jobs, freeTime);
			currentMaxRating = Math.max(currentMaxRating, resultOfAllocs[0]);
			return getMaxRatingRecursive(numWorkers, 
					jobs.subList(resultOfAllocs[1], jobs.size()), 
					freeTime, currentJobPriority + resultOfAllocs[1], 
					currentMaxRating);
		}
	}
	
	/**
	 * Returns true iff no worker has enough freeTime for currentJob
	 * Represents the manager's method of allocation, so finds the best 
	 * allocation and records the result in freeTime.
	 * @param jobs
	 * @param freeTime
	 * @return
	 */
	private static Integer[] allocatePastIntern(List<Integer> jobs, Integer[] freeTime) {
		int currentMaxRating = Integer.MIN_VALUE;
		Integer[] workersToAllocate = new Integer[3];
		int numJobs = Math.min(jobs.size(), 3);
		int numWorkers = freeTime.length;
		Integer[] possibleRatings = new 
				Integer[(int) Math.pow(numWorkers, 2)];
		Arrays.fill(possibleRatings, 1);
		
		getPossibleRatingsRecursive(jobs, Arrays.copyOfRange(freeTime, 
				0, numWorkers), possibleRatings, 0, 0);
		
		Integer[] internAllocation = new Integer[]{0, 0};
		for(int i = 0; i < possibleRatings.length; i++){
			int currentRating = possibleRatings[i];
			if(currentRating < 1){
				if(numJobs == 1){
					i += numWorkers;
				}
				else if(numJobs == 3) {
					Integer[] tmpFreeTime = Arrays.copyOfRange(freeTime, 
							0, numWorkers);
					tmpFreeTime[i / numWorkers] -= jobs.get(0);
					tmpFreeTime[i % numWorkers] -= jobs.get(1);
					internAllocation = internAllocate(
							jobs.subList(2, jobs.size()), 
							tmpFreeTime);
					currentRating = internAllocation[0];
				}
				if(currentRating > currentMaxRating){
					workersToAllocate[0] = i / numWorkers;
					workersToAllocate[1] = i % numWorkers;
					workersToAllocate[2] = internAllocation[1];
					currentMaxRating = currentRating;
				}
			}
		}
		if(currentMaxRating == Integer.MIN_VALUE){
			jobs = jobs.subList(0, 1);
			return new Integer[]{currentMaxRating, 1};
		}
		freeTime[workersToAllocate[0]] -= jobs.get(0);
		if(numJobs > 1){
			freeTime[workersToAllocate[1]] -= jobs.get(1);
			if(numJobs == 3){
				freeTime[workersToAllocate[2]] -= jobs.get(2);
			}
		}
		return new Integer[] {currentMaxRating, numJobs};
	}
	
	private static Integer[] getPossibleRatingsRecursive(List<Integer> jobs,
			Integer[] freeTime, Integer[] possibleRatings, int currentIndex,
			int currentWorker) {
		int numWorkers = freeTime.length;
		
		if(currentIndex == possibleRatings.length){
			return possibleRatings;
		}
		
		int currentJob = jobs.get(0);
		
		if((possibleRatings[currentIndex] < 1 || 
				(currentIndex % numWorkers) != 0) && jobs.size() > 1){
			currentJob = jobs.get(1);
			allocateJob(freeTime, possibleRatings, 
					currentJob, currentWorker, currentIndex);
			return getPossibleRatingsRecursive(jobs, freeTime, possibleRatings,
					currentIndex + 1, currentWorker + 1);
		}
		currentWorker = currentIndex / numWorkers;
		if(allocateJob(freeTime, possibleRatings, 
				currentJob, currentWorker, currentIndex) == 2){
			return getPossibleRatingsRecursive(jobs, freeTime, possibleRatings, 
					currentIndex + numWorkers, currentWorker + 1);
		}
		if(currentWorker != 0 && 
				possibleRatings[numWorkers * (currentWorker - 1)] < 0){
			freeTime[currentWorker - 1] += currentJob;
		}
		freeTime[currentWorker] -= currentJob;
		return getPossibleRatingsRecursive(jobs, freeTime, possibleRatings,
				currentIndex, 0);
	}

	private static int allocateJob(Integer[] freeTime,
			Integer[] possibleRatings, int currentJob, int currentWorker,
			int currentIndex) {
		if(freeTime[currentWorker] - currentJob >= 0){
			Integer[] tmpFreeTime = Arrays.copyOfRange(freeTime, 
					0, freeTime.length);
			tmpFreeTime[currentWorker] -= currentJob;
			possibleRatings[currentIndex] = 
					getRating(tmpFreeTime);
		} 
		if(possibleRatings[currentIndex] == 1) {
			possibleRatings[currentIndex] = 2;
		}
		return possibleRatings[currentIndex];
	}

	/**
	 * Returns true iff no worker has enough freeTime for currentJob
	 * Represents the "hostile" intern, so finds the worst allocation and
	 * records the result in freeTime.
	 * @param integer
	 * @param freeTime
	 * @return
	 */
	private static Integer[] internAllocate(List<Integer> jobs, 
			Integer[] freeTime) {
		int currentMinRating = 1;
		int workerToAllocate = -1;
		int currentJob = jobs.get(0);
		
		for(int worker = 0; worker < freeTime.length; worker++){
			if((freeTime[worker] - currentJob) >= 0 ){
				Integer[] tmpFreeTime = Arrays.copyOfRange(freeTime, 
						0, freeTime.length);
				tmpFreeTime[worker] -= currentJob;
				
				int currentRating = getRating(tmpFreeTime);
				if(currentRating < currentMinRating){
					currentMinRating = currentRating;
					workerToAllocate = worker;
				}
			}
		}
		freeTime[workerToAllocate] -= currentJob;
		return new Integer[] {currentMinRating, workerToAllocate};
	}
	
	/**
	 * Calculates the current rating for a given lost of freeTimes for the workers
	 * @param freeTime
	 * @return
	 */
	private static int getRating(Integer[] freeTime) {
		int currentMaxRating = 0;
		for(int i = 0; i < freeTime.length; i++){
			currentMaxRating -= Math.pow(freeTime[i], 2);
		}
		return currentMaxRating;
	}
}
