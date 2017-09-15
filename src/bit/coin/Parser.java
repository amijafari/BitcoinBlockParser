package bit.coin;

import bit.coin.model.Block;
import bit.coin.model.Block.Transaction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private static BlockParser bp;
	private static boolean writeToFile;
	private static int commandCount;
	private static String ROOT_DIR;
	
	public static void main(String[] args) throws Exception {
		
		ROOT_DIR = Parser.class.getClassLoader().getResource("").getFile().replace("/bin", "");
		
		System.out.println("===== Bitcoin BlockChain Parser =====\n");

		System.out.println("Choose Command:");
		System.out.println("read <n> \t\tRead n blockchain files.");
		System.out.println("blocks \t\t\tGet total number of parsed blocks.");
		System.out.println("print <n> \t\tPrint n blocks.");
		System.out.println("transactions <n> \tGet n's block transactions number.");
		System.out.println("transactionsA <addr> \tGet all transactions from address.");
		System.out.println("transactionsB <n> \tAll transactions with at least n Bitcoin value.");
		System.out.println("balance <n> \t\tAddresses balance at least n.");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String command = "";
		try {
			while ((command = br.readLine()) != null) 
			{
				String[] cmd = command.trim().replaceAll("\\s+", " ").split(" ");
				
				if (cmd[0].equals("read")) {
					System.out.println("Please Wait...");
					read(Integer.parseInt(cmd[1]));
				}
				else if (cmd[0].equals("blocks")) {
					blocksCount();
					commandCount++;
				}
				else if (cmd[0].equals("print")) {
					printBlocks(Integer.parseInt(cmd[1]));
					commandCount++;
				}
				else if (cmd[0].equals("transactions")) {
					transactionsCount(Integer.parseInt(cmd[1]));
					commandCount++;
				}
				else if (cmd[0].equals("transactionsA")) {
					transactionsByAddress(cmd[1]);
					commandCount++;
				}
				else if (cmd[0].equals("transactionsB")) {
					transactionsByValue(Integer.parseInt(cmd[1]));
					commandCount++;
				}
				else if (cmd[0].equals("balance")) {
					addressesByValue(Integer.parseInt(cmd[1]));
					commandCount++;
				}
				else if (cmd[0].equals("file")) {
					writeToFile = !writeToFile;
				}
				else if (cmd[0].equals("exit")) {
					System.exit(0);
				}
				else {
					System.out.println("Invalid Command!");
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private static void printBlocks(int n) {
		String out = "";
		
		out = bp.getBlocks().get(n+1).toString();
		
		present(out);
	}

	private static void transactionsByAddress(String addr) {
		String out = "";
		
		for (Block block : bp.getBlocks()) {
			List<Transaction> txs = block.findTransactionsByAddress(addr);
			
			for (Transaction tx : txs) {
				out += tx.toString();
			}
		}
		
		present(out);
	}
	
	private static void transactionsByValue(long value) {
		String out = "";
		
		for (Block block : bp.getBlocks()) {
			List<Transaction> txs = block.findTransactionsByValue(value);
			
			for (Transaction tx : txs) {
				out += tx.toString();
			}
		}
		
		present(out);
	}
	
	private static void addressesByValue(long value) {
		String out = "";
		
		for (int i = 20000; i < 25000; i++) {
			Block block = bp.getBlocks().get(i);
			List<String> addrs = block.findAddressesByValue(value);
			
			for (String addr : addrs) {
				out += "Address (Pub Key): " + addr + "\n";
			}
		}
		
		present(out);
	}

	private static void transactionsCount(int blockIndex) {
		String out = "";
		if (blockIndex > bp.getBlocksCount()) {
			out = "Block index out of range!";
		}
		else {
			out = "Transactions in block No. " + blockIndex + ": " + bp.getBlocks().get(blockIndex-1).getTransactionsCount();
		}
		
		present(out);
	}

	private static void read(int n) {
		List<String> filesPath = new ArrayList<String>();
		
		String dataBasePath = ROOT_DIR + "data/";
		
		for (int i = 0; i < n; i++) {
			String fileName = "blk" + String.format("%05d", i) + ".dat";
			filesPath.add(dataBasePath + fileName);
		}
		
		bp = new BlockParser(filesPath);
		
		System.out.println(n + " file(s) parsed successfuly!");
	}
	
	private static void blocksCount() {
		present(String.format("%d blocks have been parsed.", bp.getBlocksCount()));
	}
	
	private static void present(String out) {
		if (writeToFile) {
			writeToFile(out);
		}
		else {
			System.out.println(out);
		}
	}
	
	private static void writeToFile(String content) {
		try (PrintWriter out = new PrintWriter(ROOT_DIR + "out/" + (commandCount+1) + ".txt")) {
		    out.println(content);
		    System.out.println("result written to file successfuly!");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
