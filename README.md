# 📊 **Ex4: Advanced Spreadsheet System**

## **📌 Project Overview**

This project is an **advanced spreadsheet system** designed as part of the **Introduction to Computer Science (I2CS) 2025A** course at Ariel University. It extends traditional spreadsheet functionalities by adding **mathematical operations, conditional logic, and better error handling**.

### **Main Features:**
✔ **Support for Range2D operations** (sum, min, max, multiply, average).  
✔ **Implementation of `if` statements** for conditional logic.  
✔ **Validation and error handling** for formulas and functions.  
✔ **Detection and handling of circular references.**  
✔ **Saving and loading spreadsheet data.**

This spreadsheet allows users to **work with text, numbers, and mathematical formulas**, while also providing **useful functions** to help them in their **daily tasks and calculations**. Users can perform operations like adding numbers, checking conditions with `if` statements, and processing data using different mathematical functions.

---

## 🎥 Watch How This Project Works:

👉 **Click the image below** to watch a short video demonstrating how this project works:

[![Watch the video](https://img.youtube.com/vi/RPGV0H_u5x4/0.jpg)](https://www.youtube.com/watch?v=RPGV0H_u5x4)

---

## **🚀 Features & Functionality**

### **1️⃣ 2D Ranges (`Range2D`)**

- Allows specifying a **range of cells** (e.g., `A1:C5`).
- Used by functions like `=sum(A1:C5)` to add up values from multiple cells.

### **2️⃣ Mathematical Functions**

These functions work with **ranges of numbers**:

- `=min(A1:C5)` → Returns the **smallest value** in the range.
- `=max(A1:C5)` → Returns the **largest value** in the range.
- `=sum(A1:C5)` → Adds up all the values in the range.
- `=average(A1:C5)` → Returns the **average** value of the range.
- `=multiply(A1:C5)` → Multiplies all the values in the range together.

### **3️⃣ Conditional Functions (`if` statements)**

- The `if` function allows conditions:
  ```
  =if(A1>10,High,Low)
  ```
- The `if` function can also contain another `if` inside it to check multiple conditions:
  ```
  =if(A1>10,=if(B1<5,50,Check),Low)
  ```
  This means:
  - If `A1` is greater than `10`, it then checks `B1`.
  - If `B1` is less than `5`, it returns `50`, otherwise, it returns "Check".
  - If `A1` is not greater than `10`, it returns "Low".

### **4️⃣ Error Handling & Validation**

- **`IF_ERR`** → Invalid `if` syntax (e.g., `=if(A1,5,10)` is not allowed).
- **`FUNC_ERR`** → Not valid formed function calls (`=sum(A1)` instead of `=sum(A1:A5)`).
- **Circular references** are **detected** (e.g., `A1 = if(A1>3,2,4)`).
- **If a range contains empty cells, they are ignored, but if a range contains text, it triggers `FUNC_ERR`.**

---

## **✅ Important Rules for Functions & IF Statements**

1. **Functions and `if` statements must start with `=`** (e.g., `=if(A1>5,10,20)`).
2. **Functions and `if` statements cannot contain spaces** (e.g., `=sum(A1:B1)` is valid, but `= sum ( A1 : B1 )` is not).
3. **The `if` condition must be structured as** `validformula_operator_valid_formula` (e.g., `A1>5`, `B1==C1`).
4. **`if_true` and `if_false` can be** a number, text, formula, function, or another valid `if` statement.
5. **If a function references itself within its range, it results in a circular error (`ERR_CYCLE`).** This applies to both **ranges and `if` statements**.
6. **Empty cells in a function range are allowed**, but if any cell in the range contains text, it results in a `FUNC_ERR`.

---

## **📝 Function Validations & Usage**

### **✅ Valid Functions:**

✔ `=if(A1>5,10,20)`
✔ `=if(A1*A2!=A3/(2-A1),A2+2,A1+1)`
✔ `=sum(A1:C5)`, `=min(A1:B4)`, `=max(A2:D3)`, `=multiply(A1:B3)`

### **❌ Invalid Functions:**

❌ `=if(A1>5,10)` *(Missing **`false`** case)*
❌ `=if(A1,5,10)` *(Condition must be a valid comparison)*
❌ `=min(A1)` *(Must specify a range)*
❌ `=sum(A1:A5,B1:B5)` *(Multiple ranges not supported)*
❌ `=multiply(A1:A5,B1:B5)` *(Multiple ranges not supported)*

---

## 🔬 Testing & Edge Cases

This project includes **detailed JUnit tests** to check that everything works correctly and handles errors well.

### ✅ What the Tests Cover:
✔ **Invalid IF statements** – Testing incorrect conditions, missing parts, wrong data types, and nested IF logic.  
✔ **Range calculations** – Checking empty cells, text values, mixed data types, and large ranges are handled.  
✔ **Formulas & expressions** – Making sure formulas are read correctly, operators work as expected, and invalid references are caught.  
✔ **Circular references** – Detecting loops where formulas depend on each other, stopping infinite calculations.  
✔ **Edge cases in operations** – Handling negative numbers, empty values, and unusual inputs.  
✔ **Spreadsheet updates** – Checking how the spreadsheet changes when cells that depend on each other are updated.  

```

