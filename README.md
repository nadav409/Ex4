# 📊 **Ex4: Advanced Spreadsheet System**

## **📌 Project Overview**

This project is an **advanced spreadsheet system** that extends traditional spreadsheet functionalities with support for:

✔ **Mathematical operations on ranges** (sum, min, max, multiply, average).
✔ **Conditional logic (****`if`**** statements)** for dynamic calculations.
✔ **Comprehensive error handling** for invalid functions and circular dependencies.
✔ **Persistence** through file save and load capabilities.

The spreadsheet supports **formulas and functions**, allowing complex computations to be performed across multiple cells. This system ensures **robust formula validation** and prevents circular references.

## **✅ Important Rules for Functions & IF Statements**

1. **Functions and ****`if`**** statements must start with ****`=`** (e.g., `=if(A1>5,10,20)`).
2. **Functions and ****`if`**** statements cannot contain spaces** (e.g., `=sum(A1:B1)` is valid, but `= sum ( A1 : B1 )` is not).
3. **The ****`if`**** condition must be structured as** `valid_formula operator valid_formula` (e.g., `A1>5`, `B1==C1`).
4. **`if_true`**** and ****`if_false`**** can be** a number, text, function, or another valid `if` statement.
5. **If a function references itself within its range, it results in a circular error** (`ERR_CYCLE`). This applies to both **ranges and ****`if`**** statements**.
6. **Empty cells in a function range are allowed**, but if any cell in the range contains text, it results in a `FUNC_ERR`.

---

## **🚀 Features & Functionality**

### **1️⃣ 2D Ranges (****`Range2D`****)**

- Allows specifying a **range of cells** (e.g., `A1:C5`).
- Used by functions like `=sum(A1:C5)` to aggregate data over multiple cells.

### **2️⃣ Mathematical Functions**

These functions operate over **numeric cell ranges**:

- `=min(A1:C5)` → Returns the **minimum value** in the range.
- `=max(A1:C5)` → Returns the **maximum value** in the range.
- `=sum(A1:C5)` → Returns the **sum** of all values in the range.
- `=average(A1:C5)` → Returns the **average** value.
- `=multiply(A1:C5)` → Returns the **product** of all values in the range.

### **3️⃣ Conditional Functions (****`if`**** statements)**

- The `if` function enables conditional logic:
  ```
  =if(A1>10, "High", "Low")
  ```
- Supports **nested conditions**:
  ```
  =if(A1>10, if(B1<5, "OK", "Check"), "Low")
  ```

### **4️⃣ Error Handling & Validation**

- **`IF_ERR`** → Invalid `if` syntax (e.g., `=if(A1,5,10)` is invalid).
- **`FUNC_ERR`** → Malformed function calls (`=sum(A1)` instead of `=sum(A1:A5)`).
- **Circular references** are **detected** (e.g., `A1 = if(A1>3, 2, 4)`).
- **If a range contains empty cells, they are ignored, but if a range contains text, it triggers ****`FUNC_ERR`**.

---

## **📝 Function Validations & Usage**

### **✅ Valid Functions:**

✔ `=if(A1>5, 10, 20)`
✔ `=if(A1*A2 != A3/(2-A1), A2+2, A1+1)`
✔ `=sum(A1:C5)`, `=min(A1:B4)`, `=max(A2:D3)`, `=multiply(A1:B3)`

### **❌ Invalid Functions:**

❌ `=if(A1>5,10)` *(Missing **`false`** case)*
❌ `=if(A1, 5, 10)` *(Condition must be a valid comparison)*
❌ `=min(A1)` *(Must specify a range)*
❌ `=sum(A1:A5,B1:B5)` *(Multiple ranges not supported)*
❌ `=multiply(A1:A5,B1:B5)` *(Multiple ranges not supported)*

---

## **🔬 Testing & Edge Cases**

This project includes **rigorous JUnit tests** for:
✔ `ifCondition()`, `ifTrue()`, `ifFalse()`, `evaluateCondition()`
✔ `depth()`, `isFormP()`, `findLastOp()`
✔ `Range2D` operations: `min`, `max`, `sum`, `average`, `multiply`
✔ Handling **invalid inputs and edge cases**
✔ Circular reference detection in `depth()`

**Example test case for ****`multiply`**** function:**

```java
@Test
void testMultiplyFunction() {
    sheet.set(0, 0, "2");
    sheet.set(1, 0, "3");
    sheet.set(2, 0, "=multiply(A1:B1)");
    assertEquals("6.0", sheet.value(2, 0));
}
```

