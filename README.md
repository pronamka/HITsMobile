# HITsMobile - A Scratch-Inspired Programming Language for Mobile

**HITsMobile** is a custom-built programming language and interpreter designed for mobile platforms. Inspired by the visual simplicity of Scratch, HITsMobile allows users to write, evaluate, and interact with code in a user-friendly and structured way — all while retaining the power of typed variables, expressions, and scoped blocks.

This project is written in Kotlin and tailored for use in mobile apps (e.g., via Android). It supports a growing set of features that make it fun and safe to experiment with programming logic on the go.

---

## ✨ Features

- **Typed Variables**  
  Supports `Int`, `Double`, `String`, `Bool`, and user-defined arrays with enforced element types.

- **Custom Arrays**  
  Arrays can be declared with a fixed size and typed elements (e.g., `Int[10]`). Element types are checked at initialization and assignment.

- **Variable Scoping**  
  Each code block (`if`, `else`, `while`, etc.) introduces a new scope. Variable declarations within a scope are discarded when the scope exits, while modified outer-scope variables retain changes.

- **User-Defined Functions**
  Supports defining and invoking custom functions with parameters, local variables, and return values.

- **Operations and Expressions**  
  Includes arithmetic, comparison, and boolean operations.

- **Statements**  
  Includes `print`, `declaration`, `assignment`, control flow statements (`if`, `else`, `while`), and more.

- **Extensible Evaluation System**  
  Statements and expressions implement interfaces like `IStatement` and `IOperation`, and are evaluated against a dynamic variable repository stack.

---


HITsLanguage supports the following language constructs:

```plaintext
func max(arr Int[]): Int{
  max_value Int = -9999999
  for (i Int = 0; i < arr.size(); i = i+1){
    if (arr[i]>max_value){
      max_value = arr[i]
    }
  }
  return max_value
}


a: Int = 5
b: Int = 3
c: Double = PI
name: String = "Alice"
flag: Bool = true
nums: Int[3] = [1, 2, 3]
nums[1] = 42
if (a > 3) {
    print("a is greater than 3");
} else if (a == 3) {
    print("a is equal to 3");
} else{
    print("a is less than 3")
}

print("Maximum in the array nums is: " + max(nums).toString())
```
