/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

import omada17.newyorkapp.*;
public class red {

	public static void main(String[] args) {
		while(true)
		{
			ReduceWorker r = new ReduceWorker("ReduceWorker", Constants.REDUCER_PORT);
			r.initialize();
			r.waitForMasterAck();
			r.close();
			System.out.println("\n------------------------------\n");
		}

	}

}
