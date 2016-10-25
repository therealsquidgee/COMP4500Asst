package assignment2.test;

import assignment2.*;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Basic tests for the {@link RecursiveAllocation} implementation class.
 * 
 * We will use a more comprehensive test suite to test your code, so you should
 * add your own tests to this test suite to help you to debug your
 * implementation.
 */
public class RecursiveAllocationTest {

	@Test
	public void basicTest() {

		int numWorkers = 2;
		int maxHours = 10;
		List<Integer> jobs = new ArrayList<>();
		jobs = new ArrayList<>();
		jobs.add(6);
		jobs.add(5);
		jobs.add(3);
		jobs.add(1);
		jobs.add(7);

		int expected = -17;
		int actual = RecursiveAllocation.maxRatingRecursive(numWorkers,
				maxHours, jobs);
		Assert.assertEquals(expected, actual);
	}

}
