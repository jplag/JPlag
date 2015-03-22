package jplag;

import java.io.File;

import jplag.clustering.Cluster;
import jplag.options.Options;

/**
 * This interface contains the smallest amount of methods which are needed to call from outside to generate a report 
 * @author Markus.Schuhmacher
 *
 */
public interface IReport
{
	/**
	 * This method creates a report and writes the content into directory f. 
	 * @param f A File object of the directory where the result should be saved to
	 * @param dist This array saves how many AllMatches occurrences were found totally - saved in 10% intervals
	 * @param avgmatches AllMatches objects sorted by the averege percentage of both submissions in the AllMatches object (but only n objects)
	 * @param maxmatches AllMatches objects sorted by the maximum percentage of both submissions in the AllMatches object (but only n objects)
	 * @param minmatches AllMatches objects sorted by the minimum percentage of both submissions in the AllMatches object (but only n objects)
	 * @param clustering The root Cluster element of the cluster tree
	 * @param options the {@link Options} object
	 * @throws jplag.ExitException
	 */
	public void write(File f, int[] dist,
		SortedVector<AllMatches> avgmatches,
		SortedVector<AllMatches> maxmatches,
		SortedVector<AllMatches> minmatches, Cluster clustering,
		Options options) throws jplag.ExitException;
}