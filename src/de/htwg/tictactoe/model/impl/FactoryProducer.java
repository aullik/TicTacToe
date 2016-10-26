package de.htwg.tictactoe.model.impl;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class FactoryProducer {
	
	private FactoryProducer() {
	} 
	
	public static AbstractWinStateStrategyFactory getOneDimensionFactory(){
		return new OneDimensionGridsStateStrategyFactory();
	}
	public static AbstractWinStateStrategyFactory getThreeDimensionFactory(){
		return new ThreeDimensionGridsStateStrategyFactory();
	}
}
