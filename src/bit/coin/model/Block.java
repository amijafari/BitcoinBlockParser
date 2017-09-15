package bit.coin.model;

import bit.coin.util.ByteUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

	private String magicNum;
	private int blockSize;
	private BlockHeader blockHeader;
	private long txCount;
	private List<Transaction> transactions;
	private boolean hasNextBlock = true;
	
	public Block(InputStream block) throws Exception {
		if (hasLength(block, 8)) {
			magicNum = ByteUtil.readHex(block, 4);
			blockSize = ByteUtil.readInt(block, 4);
		} 
		else {
			hasNextBlock = false;
			return;
		}
		
		if (hasLength(block, blockSize)) {
			setHeader(block);
			
			txCount = ByteUtil.readVarInt(block);
			
			transactions = new ArrayList<Transaction>();

			for (long i = 0; i < txCount; i++) {
				transactions.add(new Transaction(block));
			}
		} 
		else {
			hasNextBlock = false;
		}
	}
		
	public void setHeader(InputStream block) throws Exception {
		this.blockHeader = new BlockHeader(block);
	}
	
	public boolean hasNextBlock() {
		return hasNextBlock;
	}
	
	public long getTransactionsCount() {
		return txCount;
	}
	
	private boolean hasLength(InputStream in, long length) {
		try {
			return (in.available() >= length);
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public List<Transaction> findTransactionsByAddress(String addr) {
		List<Transaction> rs = new ArrayList<Transaction>();
		
		for (Transaction tx : transactions) {
			for (TransactionOutput out : tx.outputs) {
				if (ByteUtil.bytesToHex(out.pubKey).equalsIgnoreCase(addr)) {
					rs.add(tx);
				}
			}
		}
		
		return rs;
	}
	
	public List<Transaction> findTransactionsByValue(long value) {
		List<Transaction> rs = new ArrayList<Transaction>();
		
		for (Transaction tx : transactions) {
			for (TransactionOutput out : tx.outputs) {
				if (out.value/100000000 >= value) {
					rs.add(tx);
				}
			}
		}
		
		return rs;
	}
	
	public List<String> findAddressesByValue(long value) {
		List<String> rs = new ArrayList<String>();
		
		for (Transaction tx : transactions) {
			for (TransactionOutput out : tx.outputs) {
				if (out.value/100000000 >= value) {
					rs.add(ByteUtil.bytesToHex(out.pubKey));
				}
			}
		}
		
		return rs;
	}
	
	public String toString() {
		String r = "";
		r += String.format("Magic No: \t%s\n", magicNum);
		r += String.format("Block Size: \t%d\n", blockSize);
		r += String.format("\n");
		
		r += blockHeader.toString();
		
		r += "\n";
		r += String.format("===== Tx Count: %d\n", txCount);
		for (Transaction t : transactions) {
			r += t.toString();
		}
		
		return r;
	}
	
	// BlockHeader class
	private class BlockHeader {
		private int version;
		private String prevHash;
		private String merkleHash;
		private Date time;
		private long bits;
		private long nonce;
		
		public BlockHeader(InputStream block) throws Exception {
			version = ByteUtil.readInt(block, 4);
			prevHash = ByteUtil.readHex(block, 32);
			merkleHash = ByteUtil.readHex(block, 32);
			time = new Date(ByteUtil.readLong(block, 4)*1000);
			bits = ByteUtil.readLong(block, 4);
			nonce = ByteUtil.readLong(block, 4);
		}
		
		public String toString() {
			String r = "";
			r += String.format("########## Block Header ##########\n");
			r += String.format("Version: \t%d\n", version);
			r += String.format("Previous Hash: \t%s\n", prevHash);
			r += String.format("Merkle Root: \t%s\n", merkleHash);
			r += String.format("Time: \t\t%s\n", time);
			r += String.format("Difficulty: \t%s\n", bits);
			r += String.format("Nonce: \t\t%s\n", nonce);
			
			return r;
		}
	}
	
	public class Transaction {
		
		private int version;
		private long lockTime;
		private long inCount;
		private long outCount;
		private List<TransactionInput> inputs;
		private List<TransactionOutput> outputs;

		
		private Transaction(InputStream block) throws Exception {
			version = ByteUtil.readInt(block, 4);
			
			inCount = ByteUtil.readVarInt(block);
			inputs = new ArrayList<TransactionInput>();
			
			for (int i = 0; i < inCount; i++) {
				inputs.add(new TransactionInput(block));
			}
			
			outCount = ByteUtil.readVarInt(block);
			outputs = new ArrayList<TransactionOutput>();
			
			if (outCount > 0) {
				for (int i = 0; i < outCount; i++) {
					outputs.add(new TransactionOutput(block));
				}
			}
			
			lockTime = ByteUtil.readLong(block, 4);
		}
		
		public String toString() {
			String r = "\n";
			r += String.format("========== Transaction ==========\n");
			r += String.format("Tx Version: \t%d\n", version);
			
			r += String.format("=== Inputs: \t%d\n", inCount);
			for (TransactionInput i : inputs) {
				r += i.toString();
			}
			
			r += String.format("=== Outputs: \t%d\n", outCount);
			for (TransactionOutput o : outputs) {
				r += o.toString();
			}
			
			r += String.format("Lock Time: \t%d\n", lockTime);
			
			return r;
		}
	}
	
	private class TransactionInput {
		private String prevHash;
		private String txOutId;
		private long scriptLen;
		private byte[] scriptSig;
		private String seqNo;
		
		private TransactionInput(InputStream block) throws Exception {
			prevHash = ByteUtil.readHex(block, 32);
			txOutId = ByteUtil.readHex(block, 4);
			scriptLen = ByteUtil.readVarInt(block);
			scriptSig = ByteUtil.readByte(block, scriptLen);
			seqNo = ByteUtil.readHex(block, 4);
		}
		
		public String toString() {
			String r = "";
			r += String.format("Previous Hash: \t%s\n", prevHash);
			r += String.format("Tx Out Index: \t%s\n", txOutId);
			r += String.format("Script Length: \t%d\n", scriptLen);
			r += String.format("Script Sig: \t%s\n", ByteUtil.bytesToHex(scriptSig));
			r += String.format("Sequence: \t%s\n", seqNo);
			
			return r;
		}
	}
	
	private class TransactionOutput {
		private long value;
		private long scriptLen;
		private byte[] pubKey;
		
		private TransactionOutput(InputStream block) throws Exception {	
			value = ByteUtil.readLong(block, 8);
			scriptLen = ByteUtil.readVarInt(block);
			pubKey = ByteUtil.readByte(block, scriptLen);
		}

		public String toString() {
			String r = "";
			r += String.format("Value: \t\t%d\n", value);
			r += String.format("Script Len: \t%d\n", scriptLen);
			r += String.format("Pubkey: \t%s\n", ByteUtil.bytesToHex(pubKey));
			
			return r;
		}
	}
	
}
