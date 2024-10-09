/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

/**
 *
 * @author dd200138d
 */
import rs.etf.sab.operations.PackageOperations.Pair;

public class PairClass<A,B> implements Pair<A, B> {
    public A first;
    public B second;

    public PairClass(A first, B second) {
        this.first = first;
        this.second = second;
    }

	@Override
	public A getFirstParam() {
		return first;
	}

	@Override
	public B getSecondParam() {
		return second;
	}
}
