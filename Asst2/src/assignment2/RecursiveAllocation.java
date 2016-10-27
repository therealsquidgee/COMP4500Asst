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
		// Set up a tree to track freeTime at different jobPriority levels.
		Integer[][] freeTimes = 
				new Integer[(int) Math.pow(numWorkers, jobs.size())]
						[numWorkers + 1];
		for(int i = 0; i < numWorkers; i++){
			freeTimes[0][i] = maxHours;
		}
		if(maxHours - jobs.get(0) < 0){
			return (int) (0 - Math.pow(maxHours, 2)) * numWorkers;
		}
		freeTimes[0][0] -= jobs.get(0);
		freeTimes[0][numWorkers] = 1;
		
		return getPossibleFreeTimesRecursive(jobs.subList(1, jobs.size()), 
				freeTimes, Arrays.copyOfRange(freeTimes, 0, 1), 1, 0, 0, false, 
				getRating(freeTimes[0]), 1);
	}
	
	/**
	 * Recurses through the freeTimes tree, keeping track of the 
	 * maximum minimum value possible after an intern pick.
	 * @param jobs
	 * The list of jobs left to be allocated.
	 * @param freeTimes
	 * The Array of free time arrays.
	 * Each free time array contains the free time for each worker 
	 * and the jobPriority of the last free time change or -1 if that
	 * index is part of the "dead" parts of the tree.
	 * @param parents
	 * An array of the parents free times for the current level of recursion.
	 * @param currentJobPriority
	 * The current Job Priority being assigned.
	 * @param currentParent
	 * The current parent whose children are being assigned a job.
	 * @param currentWorker
	 * The current worker being assigned a job.
	 * @param oneAllocated
	 * A boolean that is true if one job has been allocated at this level
	 * (used to end the recursion early if no job can be allocated)
	 * @param currentMax
	 * The current Max Rating.
	 * @param currentMin
	 * The current min rating for this group of intern allocations. 
	 * @return
	 */
	private static int getPossibleFreeTimesRecursive(List<Integer> jobs,
			Integer[][] freeTimes, Integer[][] parents, 
			int currentJobPriority, int currentParent, int currentWorker,
			boolean oneAllocated, int currentMax, int currentMin){
		
		// If no jobs left to allocate, return currentMax
		if(jobs.size() == 0){
			return currentMax;
		}
		
		// The number of workers and the number of children for this 
		//job priority
		int numWorkers = freeTimes[0].length - 1;
		int numChildren = (int) Math.pow(numWorkers, currentJobPriority);
		
		if(currentParent < parents.length){
			//Checks if this parent wasn't made unassignable earlier on.
			if(parents[currentParent][numWorkers] == currentJobPriority){
				if(currentWorker < numWorkers){
					//The current tree index for the worker being assigned.
					int treeIndex = (numWorkers * currentParent) + 
							currentWorker;
					// Copy the values of the parent here for now.
					freeTimes[treeIndex] = 
							Arrays.copyOfRange(parents[currentParent], 
									0, parents[currentParent].length);
					if(allocateJob(jobs.get(0), freeTimes, 
							currentParent, currentWorker, currentJobPriority)){
						oneAllocated = true;
						if(currentJobPriority < 3){
							currentMax = Math.max(currentMax, 
									getRating(freeTimes[treeIndex]));
						}
						// If child allocated check if it's the min for this 
						//recursion
						currentMin = Math.min(currentMin, 
								getRating(freeTimes[treeIndex]));
					}
					// Otherwise check if the parent is still the current min.
					else{
						currentMin = Math.min(currentMin, 
								getRating(freeTimes[currentParent]));
					}
					// Check the next worker.
					return getPossibleFreeTimesRecursive(
							jobs, freeTimes, parents, currentJobPriority, 
							currentParent, currentWorker + 1, oneAllocated, 
							currentMax, currentMin);
				}
			}
			if((currentParent + 1) % 
					(Math.pow(2, (currentJobPriority % 3))) == 0){
				// If we are moving on to the next group of values affected
				// By the hostile intern.
				if(currentMin != 1){
					// Check if we have a new max
					currentMax = Math.max(currentMax, currentMin);
				}
				// Set the current min back to 1.
				currentMin = 1;
			}
			// Move along to the next parent.
			return getPossibleFreeTimesRecursive(
					jobs, freeTimes, parents, currentJobPriority, 
					currentParent + 1, 0, oneAllocated, currentMax, 
					currentMin);
		}
		// If no job was allocated, return the current max rating.
		if(!oneAllocated){
			return currentMax;
		}
		// Recurse to the next job to assign.
        return getPossibleFreeTimesRecursive(
        		jobs.subList(1, jobs.size()), freeTimes, 
        		Arrays.copyOfRange(freeTimes, 0, numChildren), 
        		currentJobPriority + 1, 0, 0, false, currentMax, 1);
	}
	
	/**
	 * Allocates a given job and jobPriority to a worker
	 * 
	 * Or sets the job priority for that worker to -1,
	 * precluding attempts to allocate the worker's children.
	 *
	 */
	private static boolean allocateJob(Integer job, Integer[][] freeTimes,
			int currentParent, int worker, int currentJobPriority) {
		int numWorkers = freeTimes[0].length - 1;
		int treeIndex = (numWorkers * currentParent) + worker;
		if(freeTimes[treeIndex][worker] - job >= 0){
			freeTimes[treeIndex][worker] -= job;
			freeTimes[treeIndex][numWorkers] = currentJobPriority + 1;
			return true;
		}
		freeTimes[treeIndex][numWorkers] = -1;
		return false;
	}
	
	/**
	 * Gets the rating for a given worker's free time.
	 */
	private static int getRating(Integer[] freeTime) {
		int currentMaxRating = 0;
		for(int i = 0; i < freeTime.length - 1; i++){
			currentMaxRating -= Math.pow(freeTime[i], 2);
		}
		return currentMaxRating;
	}
}
