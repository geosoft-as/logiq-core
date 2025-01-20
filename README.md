# What is GeoSoft LogIQ?

GeoSoft LogIQ is a cloud-based time series data server.
It accepts time series data from _producers_ and deliver the data to subscribing _consumers_ in real-time or later.

The communication with the server is done over a secure web socket connection sending simple strings in the
[JSON-RPC 2.0](https://www.jsonrpc.org/specification)
format where the data themselves are included as
[TimeSeries.JSON](https://github.com/geosoft-as/timeseries).

LogIQ is available on the following URL:

> [!NOTE]
> wss://logiq.geosoft.no:8025



# What is logiq-core?

The main reason for maintaining the `logic-core` repository is the present documentation.

Communicating with the LogIQ server is very simple, the full set of requests and responses are documented in detail below.

But to simplify the implementation of producers and consumers further, this repository contains code that can be used in
specific programming environments.

Note that the `logiq-core` library is being used both by the LogIQ server and the GeoSoft desktop consumer environment,
ScenIQ&trade; as well as in PoC client applications like [TrackMe](https://geosoft.no/TrackMe).



# The LogIQ protocol

All communication between the LogIQ server and its clients (producers and consumers) is done be simple JSON-RPC calls.
A client send _request_ and (depending on the request) receives a corresponding _response_ from the server.

The generic form of a JSON-RPC request and response is as follows:

### Request:

```txt
{
  "jsonrpc": "2.0",
  "method": "<method>",
  "params": ["<argument1>", "<argument2>", ...],
  "id": <id>
}
```

### Response:

```txt
{
  "jsonrpc": "2.0",
  "result": <result object>,
  "error": {
    "code": <error code>,
    "message": <error message>,
    "data": <additional error information>
  },
  "id": <id>
}
```

Below is the full set of LogIQ methods:

## Producer methods

| method              | parameters           | used by       |
|---------------------|----------------------|---------------|
| **send**            | streamId             | producer      |
|                     | clientUsername       |               |
|                     | clientPassword       |               |
|                     | data                 |               |
|                     |                      |               |
| **resetStream**     | streamId             | producer      |
|                     | clientUsername       |               |
|                     | clientPassword       |               |


## Consumer methods

| method              | parameters           | used by       |
|---------------------|----------------------|---------------|
| **startConsuming**  | streamName           | consumer      |
|                     | clientUsername       |               |
|                     | clientPassword       |               |
|                     | messageId            |               |
|                     |                      |               |
| **stopConsuming**   | streamName           | consumer      |
|                     | clientUsername       |               |
|                     | clientPassword       |               |
|                     | messageId            |               |


## Customer administration methods

| method              | parameters           | used by       |
|---------------------|----------------------|---------------|
| **createStream**    | customerUsername     | customer      |
|                     | customerPassword     |               |
|                     |                      |               |
| **deleteStream**    | streamName           | customer      |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **getStreams**      | customerUsername     | customer      |
|                     | customerPassword     |               |
|                     |                      |               |
| **createStream**    | streamName           | customer      |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **createStream**    | customerUsername     | customer      |
|                     | customerPassword     |               |
|                     |                      |               |
| **updateStream**    | streamId             | customer      |
|                     | newName              |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **deleteStream**    | streamId             | customer      |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **getTransfers**    | streamId             | customer      |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **getClients**      | customerUsername     | customer      |
|                     | customerPassword     |               |
|                     |                      |               |
| **createClient**    | clientName           | customer      |
|                     | contact              |               |
|                     | email                |               |
|                     | username             |               |
|                     | password             |               |
|                     | clientUsername       |               |
|                     | clientPassword       |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **updateClient**    | clientId             | customer      |
|                     | newClientName        |               |
|                     | newContact           |               |
|                     | newEmail             |               |
|                     | newClientUsername    |               |
|                     | newClientPassword    |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **deleteClient**    | clientId             | customer      |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **setAsProducer**   | clientId             | customer      |
|                     | streamId             |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **setAsConsumer**   | clientId             | customer      |
|                     | streamId             |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **isProducer**      | clientId             | customer      |
|                     | streamId             |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |
|                     |                      |               |
| **isConsumer**      | clientId             | customer      |
|                     | streamId             |               |
|                     | customerUsername     |               |
|                     | customerPassword     |               |

## LogIQ administrator methods

| method              | parameters           | used by       |
|---------------------|----------------------|---------------|
| **getCustomers**    | adminUsername        | administrator |
|                     | adminPassword        |               |
|                     |                      |               |
| **createCustomer**  | name                 | administrator |
|                     | contact              |               |
|                     | email                |               |
|                     | username             |               |
|                     | password             |               |
|                     | adminUsername        |               |
|                     | adminPassword        |               |
|                     |                      |               |
| **updateCustomer**  | customerId           | administrator |
|                     | newName              |               |
|                     | newContact           |               |
|                     | newEmail             |               |
|                     | newUsername          |               |
|                     | newPassword          |               |
|                     | adminUsername        |               |
|                     | adminPassword        |               |
|                     |                      |               |
| **deleteCustromer** | customerId           | administrator |
|                     | adminUsername        |               |
|                     | adminPassword        |               |


## Error codes

If an error occurs, this is returned as part of the response. The list below shows the
possible error codes and the associated description:

| Code   | Description                 |
|--------|-----------------------------|
| -32700 | Parse error                 |
| -32600 | Invalid request             |
| -32601 | Method not found            |
| -32602 | Invalid parameters          |
| -32603 | Internal error              |
| -32001 | LogIQ - Database error      |
| -32002 | LogIQ - Invalid login       |
| -32003 | LogIQ - Invalid format      |
| -32004 | LogIQ - Incompatible format |
| -32005 | LogIQ - Unknown instance    |
| -32006 | LogIQ - Illegal access      |