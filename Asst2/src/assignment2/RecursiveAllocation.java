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
		
		Integer[][] freeTimes = 
				new Integer[(int) Math.pow(numWorkers, jobs.size())][numWorkers + 1];
		for(int i = 0; i < numWorkers; i++){
			freeTimes[0][i] = maxHours;
		}
		if(maxHours - jobs.get(0) < 0){
			return 1;
		}
		freeTimes[0][0] -= jobs.get(0);
		freeTimes[0][numWorkers] = 1;
		
		int highestAllocated = getPossibleFreeTimesRecursive(jobs.subList(1, jobs.size()), 
				freeTimes, Arrays.copyOfRange(freeTimes, 0, 1), 1);
		int currentMax = Integer.MIN_VALUE;
		for(int i = 0; i < Math.pow(2, highestAllocated); i++){
			if(freeTimes[i][numWorkers] != -1){
				int currentRating = getRating(freeTimes[i]);
				currentMax = Math.max(currentMax, currentRating);
			}
		}
		
		return currentMax;
	}

	private static int getPossibleFreeTimesRecursive(List<Integer> jobs,
			Integer[][] freeTimes, Integer[][] parents, 
			int currentJobPriority){
		int currentParent = 0;
		boolean oneJobAllocated = false;
		int numWorkers = freeTimes[0].length - 1;
		int numChildren = (int) Math.pow(numWorkers, currentJobPriority);
		
		while(jobs.size() != 0){
			if(currentParent < parents.length){
				if(parents[currentParent][numWorkers] == currentJobPriority){
					if(currentJobPriority % 3 == 0){
						if(internAllocate(jobs.get(0), freeTimes, 
							currentParent, currentJobPriority)){
							oneJobAllocated = true;
						}
					}
					else{
						for(int i = 0; i < numWorkers; i++){
							freeTimes[(numWorkers * currentParent) + i] = 
									Arrays.copyOfRange(parents[currentParent], 
											0, parents[currentParent].length);
							if(managerAllocate(jobs.get(0), freeTimes, 
									currentParent, i)){
								oneJobAllocated = true;
							}
						}
					}
				}
				currentParent++;
			}
			else if(oneJobAllocated){
				return getPossibleFreeTimesRecursive(
						jobs.subList(1, jobs.size()), 
						freeTimes, 
						Arrays.copyOfRange(freeTimes, 0, numChildren), 
						currentJobPriority + 1);
			}
			else{
				break;
			}
		}
		return currentJobPriority - 1;
	}

	private static boolean internAllocate(Integer job, Integer[][] freeTimes,
			int currentParent, int currentJobPriority) {
		int currentMinRating = 1;
		int numWorkers = freeTimes[0].length - 1;
		int treeIndexToAllocate = -1;
		
		for(int worker = 0; worker < numWorkers; worker++){
			int treeIndex = (numWorkers * currentParent) + worker;
			freeTimes[treeIndex] = 
					Arrays.copyOfRange(freeTimes[currentParent], 
							0, freeTimes[currentParent].length);
			if((freeTimes[treeIndex][worker] - job) >= 0 ){
				freeTimes[treeIndex][worker] -= job;
				freeTimes[treeIndex][numWorkers] = -1;
				int currentRating = getRating(freeTimes[treeIndex]);
				if(currentRating < currentMinRating){
					currentMinRating = currentRating;
					treeIndexToAllocate = treeIndex;
				}
			}
		}
		if(treeIndexToAllocate != -1){
			freeTimes[treeIndexToAllocate][numWorkers] = 
					currentJobPriority + 1;
			return true;
		}
		return false;
	}

	private static boolean managerAllocate(Integer job, Integer[][] freeTimes,
			int currentParent, int worker) {
		int numWorkers = freeTimes[0].length - 1;
		int treeIndex = (numWorkers * currentParent) + worker;
		if(freeTimes[treeIndex][worker] - job >= 0){
			freeTimes[treeIndex][worker] -= job;
			freeTimes[treeIndex][numWorkers] += 1;
			return true;
		}
		return false;
	}
	
	private static int getRating(Integer[] freeTime) {
	int currentMaxRating = 0;
	for(int i = 0; i < freeTime.length - 1; i++){
		currentMaxRating -= Math.pow(freeTime[i], 2);
	}
	return currentMaxRating;
}
//	
//	private static int getMaxRatingRecursive(int numWorkers, List<Integer> jobs, 
//			Integer[][] freeTimes, int currentJobPriority, int currentMaxRating) {
//		
//		// Base case
//		if(jobs.size() == 0){
//			return currentMaxRating;
//		}
//		else {
//			Integer[] resultOfAllocs = allocatePastIntern(jobs, freeTimes, 
//					currentJobPriority);
//			currentMaxRating = Math.max(currentMaxRating, resultOfAllocs[0]);
//			return getMaxRatingRecursive(numWorkers, 
//					jobs.subList(resultOfAllocs[1], jobs.size()), 
//					freeTimes, currentJobPriority + resultOfAllocs[1], 
//					currentMaxRating);
//		}
//	}
//	
//	/**
//	 * Returns true iff no worker has enough freeTime for currentJob
//	 * Represents the manager's method of allocation, so finds the best 
//	 * allocation and records the result in freeTime.
//	 * @param jobs
//	 * @param freeTimes
//	 * @param currentJobPriority 
//	 * @return
//	 */
//	private static Integer[] allocatePastIntern(List<Integer> jobs, 
//			Integer[][] freeTimes, int currentJobPriority) {
//		int currentMaxRating = Integer.MIN_VALUE;
//		Integer[] workersToAllocate = new Integer[3];
//		int numJobs = Math.min(jobs.size(), 3);
//		int numWorkers = freeTimes[0].length;
//		Integer[] possibleRatings = new 
//				Integer[(int) Math.pow(numWorkers, 2)];
//		Arrays.fill(possibleRatings, 1);
//		
//		Integer[][] parents = Arrays.copyOfRange(
//				freeTimes, 0, (int)Math.pow(numWorkers, currentJobPriority));
//		for(int i = 0; i < parents.length; i++){
//			for(int j = 0; j < numWorkers; j++){
//				
//			}
//		}
//		
//		getPossibleFreeTimes(jobs, freeTimes, currentJobPriority, 0);
//		
//		Integer[] internAllocation = new Integer[]{0, 0};
//		for(int i = 0; i < possibleRatings.length; i++){
//			int currentRating = possibleRatings[i];
//			if(currentRating < 1){
//				if(numJobs == 1){
//					i += numWorkers;
//				}
//				else if(numJobs == 3) {
//					Integer[] tmpFreeTime = Arrays.copyOfRange(freeTimes, 
//							0, numWorkers);
//					tmpFreeTime[i / numWorkers] -= jobs.get(0);
//					tmpFreeTime[i % numWorkers] -= jobs.get(1);
//					internAllocation = internAllocate(
//							jobs.subList(2, jobs.size()), 
//							tmpFreeTime);
//					currentRating = internAllocation[0];
//				}
//				if(currentRating > currentMaxRating){
//					workersToAllocate[0] = i / numWorkers;
//					workersToAllocate[1] = i % numWorkers;
//					workersToAllocate[2] = internAllocation[1];
//					currentMaxRating = currentRating;
//				}
//			}
//		}
//		if(currentMaxRating == Integer.MIN_VALUE){
//			jobs = jobs.subList(0, 1);
//			return new Integer[]{currentMaxRating, 1};
//		}
//		freeTimes[workersToAllocate[0]] -= jobs.get(0);
//		if(numJobs > 1){
//			freeTimes[workersToAllocate[1]] -= jobs.get(1);
//			if(numJobs == 3){
//				freeTimes[workersToAllocate[2]] -= jobs.get(2);
//			}
//		}
//		return new Integer[] {currentMaxRating, numJobs};
//	}
//	
//	private static int getLastParent(int numWorkers, int currentJobPriority) {
//		return (int) 
//				((Math.pow(numWorkers, currentJobPriority) - 1) / numWorkers);
//	}
//
//	private static void getPossibleFreeTimes(List<Integer> jobs,
//			Integer[][] freeTimes, int currentJobPriority, 
//			int currentParentIndex) {
//		int numWorkers = freeTimes[0].length;
//		
//		while(currentParentIndex < ){
//			
//		}
//		
//		/*if((currentJobPriority[currentIndex] < 1 || 
//				(currentIndex % numWorkers) != 0) && jobs.size() > 1){
//			currentJob = jobs.get(1);
//			allocateJob(freeTimes, currentJobPriority, 
//					currentJob, currentWorker, currentIndex);
//			return getPossibleFreeTimes(jobs, freeTimes, currentJobPriority,
//					currentIndex + 1, currentWorker + 1);
//		}
//		currentWorker = currentIndex / numWorkers;
//		if(allocateJob(freeTimes, currentJobPriority, 
//				currentJob, currentWorker, currentIndex) == 2){
//			return getPossibleFreeTimes(jobs, freeTimes, currentJobPriority, 
//					currentIndex + numWorkers, currentWorker + 1);
//		}
//		if(currentWorker != 0 && 
//				currentJobPriority[numWorkers * (currentWorker - 1)] < 0){
//			freeTimes[currentWorker - 1] += currentJob;
//		}
//		freeTimes[currentWorker] -= currentJob;
//		return getPossibleFreeTimes(jobs, freeTimes, currentJobPriority,
//				currentIndex, 0);*/
//	}
//
//	private static int allocateJob(Integer[] freeTime, 
//			int job, int worker) {
//		if(freeTime[worker] - job >= 0){
//			Integer[] tmpFreeTime = Arrays.copyOfRange(freeTime, 
//					0, freeTime.length);
//			tmpFreeTime[worker] -= job;
//			possibleRatings[currentIndex] = 
//					getRating(tmpFreeTime);
//		} 
//		if(possibleRatings[currentIndex] == 1) {
//			possibleRatings[currentIndex] = 2;
//		}
//		return possibleRatings[currentIndex];
//	}
//
//	/**
//	 * Returns true iff no worker has enough freeTime for currentJob
//	 * Represents the "hostile" intern, so finds the worst allocation and
//	 * records the result in freeTime.
//	 * @param integer
//	 * @param freeTime
//	 * @return
//	 */
//	private static Integer[] internAllocate(List<Integer> jobs, 
//			Integer[] freeTime) {
//		int currentMinRating = 1;
//		int workerToAllocate = -1;
//		int currentJob = jobs.get(0);
//		
//		for(int worker = 0; worker < freeTime.length; worker++){
//			if((freeTime[worker] - currentJob) >= 0 ){
//				Integer[] tmpFreeTime = Arrays.copyOfRange(freeTime, 
//						0, freeTime.length);
//				tmpFreeTime[worker] -= currentJob;
//				
//				int currentRating = getRating(tmpFreeTime);
//				if(currentRating < currentMinRating){
//					currentMinRating = currentRating;
//					workerToAllocate = worker;
//				}
//			}
//		}
//		freeTime[workerToAllocate] -= currentJob;
//		return new Integer[] {currentMinRating, workerToAllocate};
//	}
//	
//	/**
//	 * Calculates the current rating for a given lost of freeTimes for the workers
//	 * @param freeTime
//	 * @return
//	 */
}
