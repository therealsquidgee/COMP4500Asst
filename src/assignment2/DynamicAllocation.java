package assignment2;

import java.util.*;

public class DynamicAllocation {

	/**
	 * @param numWorkers
	 *            The number of workers that are available to work.
	 * 
	 *            For this dynamic programming solution you should assume that
	 *            numWorkers=2.
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
	 *         This method must be implemented using an efficient bottom-up
	 *         dynamic programming solution to the problem under the assumption
	 *         that numWorkers == 2.
	 */
	public static int maxRatingDynamic(int numWorkers, int maxHours,
			List<Integer> jobs) {
		return 1; // REMOVE THIS LINE AND IMPLEMENT THIS METHOD USING A BOTTOM-UP DYNAMIC PROGRAMMING SOLUTION
	}
}
