# Assignment :three:
By Noah Wassberg and Simon Pislar 

## Building the program and running tests
- **Compile and run tests**: `make test`
- **Compile the program**: `make all`
- **Run the program**: `make run`
- **Clean directory**: `make clean`


## Using the program
The following program is a symbolic calculator.

Navigate to the directory where the program files are and run `make all` in your terminal to compile the program. This will build all files necessary to run the program. To run the program enter `make run`, you will now be prompted to enter an expression.

When giving invalid input, the program will continue prompting until a valid input is given. 

To enter function declaration mode follow the following format `function functionName(arg1,arg2,...argn)`.
To exit function declaration mode enter the command `End` while in function declaration mode.
To print assigned variables enter the command `Vars`.
To clear all assigned variables enter the command `Clear`.
To quit the program enter the command `Quit`.

## Design decisions
- Unary functions (for example Sin or Exp) are written as `Sin(x)` or `Sin x`