# Bitcoin Blockchain Parser
Writen in JAVA to parse Bitcoin blockchain `.dat` files.

## Usage
After running `bit.coin.Parser`:
```
read <n>                Read n blockchain files.
blocks                  Get total number of parsed blocks.
print <n>               Print n blocks.
transactions <n>        Get n's block transactions number.
transactionsA <addr>    Get all transactions from address.
transactionsB <n>       All transactions with at least n Bitcoin value.
balance <n>             Addresses balance at least n.
```