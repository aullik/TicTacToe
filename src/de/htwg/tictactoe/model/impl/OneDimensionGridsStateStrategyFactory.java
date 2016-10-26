package de.htwg.tictactoe.model.impl;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
public class OneDimensionGridsStateStrategyFactory extends AbstractWinStateStrategyFactory{
	
	@Override
	public WinStateStrategyTemplate getInstance() {
		return new OneDimensionGridsStateStrategy();
	}


}
