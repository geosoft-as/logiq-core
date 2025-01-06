## Build

make


## Usage

cp lib/logiq-core.min.js <project>/lib/


Example JavaScript:

```js
  import Signal from './libs/package.js';

  const signal = new Signal();
  console.log(signal);
```

Example HTML:

```html
  <script type="module">
    import Signal from './src/libs/package.js';

    const signal = new Signal();
    console.log(signal);
  </script>
```
