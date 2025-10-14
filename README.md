 # PC Retailer Order System – Design Notes

# Overview
The system is a model of a retailer who sells preset and custom PCs, handles credit card backed orders, and conducts fulfilment analytics. OrderService is the main component and coordinates the process of making orders, cancelling the orders, fulfilling the orders and reporting and maintaining the domain objects as immutable.

## Key Design Points
Computer Models - Computer models have an interface hierarchy (ComputerModel -PresetComputerModel / CustomComputerModel) to allow late binding of mixed orders.
- Domain objects (`PresetModel`, `CustomModel`, `Customer`, `CreditCard`, `OrderLine, StandardOrder) are all immutable; the factory classes are unique (`CustomModel Factory, Credit Card Factory).
- `OrderService` maintains internal changeable state of lifecycle transitions, but provides orders as a read-only CustomerOrder interface.
- Fulfilment generates a FulfillmentPlan which sums manufacturer and custom part count; analytics are done on-a-fly to eliminate the need to re-process order history.
JUnit 5 tests are all validation, immutability, lifecycle rules, and analytics edge cases (such as alphabetical tie-breakers).

 ### Interfaces
# 1.	ComputerModel
- Purpose:   Describes a template of computer model in general that can be offered by the vendor.
- Type: It’s an interface (not a class) which means it does not provide the implementation in code.
- Rule:  All parts in the collection should be non-empty string.
- Usage: Implemented by classes like PresetModel or CustomModel to ensure consistent structure.
# Key methods:
-	getName() → gets unique name of the model.
-	getParts() → return all the correspoding parts of its model.

# 2.	CustomComputerModel
- Purpose: Defines a customizable version of a computer model that can add or remove parts..
- Type: It’s an interface that extends ComputerModel.
- Immutability: No in-place changes — every modification creates a new instance.
#Key Methods:
-	withPart(String part) → adds a part and returns a new model.
-	withoutPart(String part) → removes a part and returns a new model.

3.	PresetComputerModel
Purpose:  Marker/contract for preset vendor models (fixed bill of materials).
•	PresetComputerModel is an interface extending ComputerModel.
•	It represents a factory preset (pre-configured) computer model.
•	Used for vendor-specific models with a fixed parts list.
•	Encourages consistent access to name, parts, and manufacturer across implementations.
Adds one method:   getManufacturer() to identify the maker.
Key methods: static PresetModel of(...) – factory to build a preset model from manufacturer/name/parts.

4.	CustomerOrder
Purpose: Abstraction for an order placed by a customer.
Key methods:
•	UUID getId() – returns the unique order ID.
•	Customer getCustomer() – returns the customer details.
•	List<OrderLine> getOrderLines() – lists ordered items.
•	CreditCard getPaymentMethod() – shows the payment card used.
•	OrderStatus getStatus() – displays the order’s current state.

Core Classes
PresetModel (implements ComputerModel, PresetComputerModel)
Role: Immutable value object representing a vendor-provided model.
Key fields:
 	• String manufacturer
• String name
• SortedSet<String> parts
Important methods:
• static PresetModel of(...) – validated factory constructor
CustomModel (implements ComputerModel, CustomComputerModel)
Role: Mutable/functional value object for a user-customizable model.
Key fields:
• String name
• SortedSet<String> parts
Important methods:
• static CustomModel of(...)
• CustomComputerModel withPart(String)
• CustomComputerModel withoutPart(String)
Customer
Role: Customer entity.
•	Customer Represents a customer of a retailer (immutabel class).
•	It has two columns: id (unique ID) and name.
•	constructed with a factory method of() checking both values aren't blank.
•	Equality only depends on the identifier, which is also met by hashCode().
•	Use requireText() because we don't want null or empty inputs.
Key fields:
         String identifier
String displayName
Important methods: static Customer of(...) – factory ensuring identifiers are valid
OrderLine
Role: Line item inside an order: a model and its quantity.
Key fields:
• ComputerModel model
• int quantity
Important methods:
• static OrderLine of(...) – validates quantity (>0)
CreditCard
Role: Payment method value object with basic validation.
Key fields:
• String number
• Date expiry
• String holderName
Important methods:
• boolean isValid(Date) – expiry check and simple structure checks
StandardOrder (implements CustomerOrder)
Role: Aggregate root for orders; holds lines, payment, and lifecycle state.
Key fields:
• UUID id
• Customer customer
• List<OrderLine> orderLines
• CreditCard paymentMethod
• Date placedAt
• OrderStatus status
Important methods:
• void markCancelled()
• void markFulfilled()
Services and Factories
OrderService
Role: Application service orchestrating the order lifecycle and fulfillment.
Key fields:
• Supplier<Date> clock – injectable time source for testability
• Map<UUID, StandardOrder> orders – in-memory order store
• Map<Customer, Integer> fulfilledByCustomer – fulfilment counts per customer
• Map<String, Map<String, Integer>> fulfilledPresetCounts – manufacturer→model→count
• Map<String, Integer> fulfilledCustomParts – part→count
Key methods:
• CustomerOrder placeOrder(...) – validates and records a new order
• void cancelOrder(UUID) – transitions to CANCELLED when allowed
• FulfillmentPlan fulfillOrder(UUID) – marks as FULFILLED and updates counts
• OrderAnalytics analytics() – exposes computed insights
FulfillmentPlan
Role: Result object summarizing what was fulfilled.
Key fields:
• Map<String, Integer> presetModelCounts
• Map<String, Integer> customPartCounts
OrderAnalytics
Role: Read-only analytics over historical fulfilments.
Key methods:
• Customer largestCustomer()
• String mostOrderedPresetModel()
• String presetManufacturer()
• String mostOrderedCustomPart()
CustomModelFactory
Role: Factory managing allocation/uniqueness of custom model names.
Key fields:
• Set<String> allocatedNames
Key methods:
• CustomModel create(...) – ensures unique, valid names
• void release(CustomComputerModel) – frees the name when discarded
CreditCardFactory
Role: Factory managing allocation/registration of credit cards.
Key fields:
• Set<String> allocatedNumbers
Key methods:
• CreditCard register(...) – validates/records a new card
• void release(CreditCard) – removes a card from active set


