# PC Retailer Order System – Design Notes

# Overview
The system is a model of a retailer who sells preset and custom PCs, handles credit card backed orders, and conducts fulfilment analytics. OrderService is the main component and coordinates the process of making orders, cancelling the orders, fulfilling the orders and reporting and maintaining the domain objects as immutable.

## Key Design Points
Computer Models - Computer models have an interface hierarchy (ComputerModel -PresetComputerModel / CustomComputerModel) to allow late binding of mixed orders.
- Domain objects (`PresetModel`, `CustomModel`, `Customer`, `CreditCard`, `OrderLine, StandardOrder) are all immutable; the factory classes are unique (`CustomModel Factory, Credit Card Factory).
- `OrderService` maintains internal changeable state of lifecycle transitions, but provides orders as a read-only CustomerOrder interface.
- Fulfilment generates a FulfillmentPlan which sums manufacturer and custom part count; analytics are done on-a-fly to eliminate the need to re-process order history.
  JUnit 5 tests are all validation, immutability, lifecycle rules, and analytics edge cases (such as alphabetical tie-breakers).

## UML Class Diagram
```plantuml
@startuml

interface ComputerModel {
  +String getName()
  +Set<String> getParts()
}

interface PresetComputerModel
interface CustomComputerModel {
  +CustomComputerModel withPart(String)
  +CustomComputerModel withoutPart(String)
}

ComputerModel <|-- PresetComputerModel
ComputerModel <|-- CustomComputerModel

class PresetModel {
  -String manufacturer
  -String name
  -SortedSet<String> parts
  +static PresetModel of(...)
}

class CustomModel {
  -String name
  -SortedSet<String> parts
  +static CustomModel of(...)
  +CustomComputerModel withPart(String)
  +CustomComputerModel withoutPart(String)
}

class CustomModelFactory {
  -Set<String> allocatedNames
  +CustomModel create(...)
  +void release(CustomComputerModel)
}

PresetComputerModel <|.. PresetModel
CustomComputerModel <|.. CustomModel

class Customer {
  -String identifier
  -String displayName
  +static Customer of(...)
}

class CreditCard {
  -String number
  -Date expiry
  -String holderName
  +boolean isValid(Date)
}

class CreditCardFactory {
  -Set<String> allocatedNumbers
  +CreditCard register(...)
  +void release(CreditCard)
}

enum OrderStatus {
  PLACED
  CANCELLED
  FULFILLED
}

class OrderLine {
  -ComputerModel model
  -int quantity
  +static OrderLine of(...)
}

interface CustomerOrder {
  +UUID getId()
  +Customer getCustomer()
  +List<OrderLine> getOrderLines()
  +CreditCard getPaymentMethod()
  +Date getPlacedAt()
  +OrderStatus getStatus()
}

class StandardOrder {
  -UUID id
  -Customer customer
  -List<OrderLine> orderLines
  -CreditCard paymentMethod
  -Date placedAt
  -OrderStatus status
  +void markCancelled()
  +void markFulfilled()
}

CustomerOrder <|.. StandardOrder

class FulfillmentPlan {
  -Map<String, Map<String, Integer>> presetModelCounts
  -Map<String, Integer> customPartCounts
}

class OrderAnalytics {
  -Customer largestCustomer
  -String mostOrderedPresetModel
  -String presetManufacturer
  -String mostOrderedCustomPart
}

class OrderService {
  -Supplier<Date> clock
  -Map<UUID, StandardOrder> orders
  -Map<Customer, Integer> fulfilledByCustomer
  -Map<String, Map<String, Integer>> fulfilledPresetCounts
  -Map<String, Integer> fulfilledCustomParts
  +CustomerOrder placeOrder(...)
  +void cancelOrder(UUID)
  +FulfillmentPlan fulfillOrder(UUID)
  +OrderAnalytics analytics()
}

OrderService --> OrderLine
OrderService --> FulfillmentPlan
OrderService --> OrderAnalytics
OrderService --> CustomerOrder
OrderService --> CreditCard
OrderService --> Customer
OrderService --> ComputerModel

StandardOrder --> OrderLine
StandardOrder --> CreditCard
StandardOrder --> Customer

CustomModelFactory --> CustomModel
CreditCardFactory --> CreditCard

@enduml
```

