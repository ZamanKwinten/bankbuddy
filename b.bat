call mvn clean package
jpackage -t msi -n BankBuddy -p target/modules -m bank.buddy/bank.buddy.App --win-dir-chooser