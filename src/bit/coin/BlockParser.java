package bit.coin;

import bit.coin.model.Block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BlockParser {

	private int blocksCount;
	private List<Block> blocks = new ArrayList<Block>();
	
	public BlockParser(List<String> filesPath) {
		for (String file : filesPath) {
			parse(file);
		}
	}
	
	public int getBlocksCount() {
		return blocksCount;
	}
	
	public List<Block> getBlocks() {
		return blocks;
	}
	
	private void parse(String filePath) {
	
		InputStream in = null;
		
		try {
			in = new FileInputStream(new File(filePath));	
			
			boolean hasNextBlock = true;
			
			Block block;
			while (hasNextBlock) {
				block = new Block(in);
				
				hasNextBlock = block.hasNextBlock();
				
				if (hasNextBlock) {
					blocks.add(block);
				}
				
				blocksCount++;
			}
		}
		catch (Exception e) {
			System.out.println("Error in parsing: " + filePath);
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
