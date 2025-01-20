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

Communicating with the LogIQ server is very simple, the full set of requests and responses
are documented in detail below.

`logiq-core` consists of _optional_ code that can simplify the implementation of producers and consumers further
for given programming environments.



# The LogIQ protocol

All communication between the LogIQ server and its clients (producers and consumers) is done be simple JSON-RPC calls.
A client send _request_ and receives a corresponding _response_ from the server.

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

These are the most frequently methods, the full

| method        | parameters       |
|---------------|------------------|
| **createStream**  | customerUsername |
|               | customerPassword |
|               |                  |
| deleteStream  | streamName       |
|               | customerUsername |
|               | customerPassword |
|               |                  |
| getCustomers  | adminUsername    |
|               | adminPassword    |
|               |                  |
| createCustomer | name            |
|                | contact         |
|                | email           |
|                | username        |
|                | password        |
|                | adminUsername   |
|                | adminPassword   |
|                |                 |














## getCustomers

| getCustomers   | parameters         |
|----------------|--------------------|
|                | adminUsername      |
|                | adminPassword      |


Example request:

```json
{
  "jsonrpc": "2.0",
  "method": "getCustomers",
  "params": ["admin", "admin_pw"],
  "id": 77
}
```

Example response:

```json
{
  "jsonrpc": "2.0",
  "id": 77,
  "result": [
    {
      "id": "1001",
      "name": "Company A"
    },
    {
      "id": "1002",
      "name": "Company B"
    }
  ]
}


## createCustomer

| createCustomer | parameters         |
|----------------|--------------------|
|                | adminUsername      |
|                | adminPassword      |

