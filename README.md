<div align="center">

<img src="docs/images/logo.png" width="160" alt="Compose Script logo">

# Compose Script

**Live Jetpack Compose inside any web page. No build, no server.**

Add one script, write Compose in your HTML, and it renders in the page, powered by
[Fast Compose](https://github.com/fast-compose/fast-compose).

[![npm](https://img.shields.io/npm/v/@fast-compose/compose-script)](https://www.npmjs.com/package/@fast-compose/compose-script)
[![jsDelivr](https://data.jsdelivr.com/v1/package/npm/@fast-compose/compose-script/badge)](https://www.jsdelivr.com/package/npm/@fast-compose/compose-script)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
![Kotlin/Wasm](https://img.shields.io/badge/Kotlin-Wasm-7F52FF?logo=kotlin&logoColor=white)

</div>

---

## Quick start

Add the library to your page:

```html
<script src="https://cdn.jsdelivr.net/npm/@fast-compose/compose-script/compose-script.js"></script>
```

- Load `compose-script.js` with a plain `<script src>`, not `type="module"`: it loads its `.wasm` files from its own URL.
- The URL points to the latest release. Without the `@X.Y.Z` part, the CDN serves the newest release instead. Each
  release is built with the newest Fast Compose release of its time, named in its npm description.

Then write Compose anywhere in the page:

```html
<script type="text/compose-content">
  var count by remember { mutableStateOf<Int>(0) }
  Button(onClick = { count++ }) { Text("Clicked $count times") }
</script>
```

## Script types

| Type | Shows | Size |
|---|---|---|
| `text/compose-content` | One composable, with no labels or frames | Set by the composable |
| `text/compose-preview` | Every `@Preview` function, with labels and frames | 400 px tall by default, or set by CSS |

Each script is replaced by a `<compose-content>` or `<compose-preview>` element with the script's `id`, `class` and
`style`, so pages can style them with CSS. Parse and runtime errors appear inside the element, and each one starts
when it scrolls into view.

## compose-content

### Body of a composable

By default the code is the body of a composable: write the UI directly.

```html
<script type="text/compose-content">
  Column(Modifier.padding(16.dp)) {
      Text("Hello from Compose!")
      Button(onClick = {}) { Text("Click me") }
  }
</script>
```

To declare your own composables or classes, use `data-composable`.

### Whole file with `data-composable`

With `data-composable`, the code is a whole Kotlin file and the attribute names the function to show. The function
must be a top-level `@Composable` function without parameters; the file may declare other functions and classes.

```html
<script type="text/compose-content" data-composable="Profile">
  data class User(val name: String, val role: String)

  @Composable
  fun UserRow(user: User) {
      Column(Modifier.padding(8.dp)) {
          Text(user.name)
          Text(user.role, color = Color.Gray)
      }
  }

  @Composable
  fun Profile() {
      Column {
          UserRow(User("Ada", "Engineer"))
          UserRow(User("Grace", "Admiral"))
      }
  }
</script>
```

### Size

The composable sets the element's width and height, up to the width of its parent and the height of the window.
Content that fills or scrolls needs a size in the code, e.g. `Modifier.height(300.dp)`.

A `width` or `height` in the script's `style` attribute is kept, in any CSS unit, and the composable is measured
within it; content larger than a fixed size is cut off. The composable still sets the side that is not given.

```html
<script type="text/compose-content" style="width: 200px">
  Text("Wraps within 200 px, and the composable sets the height")
</script>
```

Sizes from stylesheets or classes are not read; to size by class, put the script in an element with that class.

## compose-preview

The code is a whole Kotlin file. Every `@Composable` function with `@Preview` is shown with its name, size, background,
font scale and night mode.

```html
<script type="text/compose-preview" style="height: 300px">
  @Preview(name = "Light", showBackground = true)
  @Composable
  fun LightGreeting() {
      Text("Hello", Modifier.padding(16.dp))
  }

  @Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
  @Composable
  fun DarkGreeting() {
      Text("Hello", Modifier.padding(16.dp))
  }
</script>
```

The element is 400 px tall by default; set another height with CSS. Previews taller than the element scroll inside it.

## License

[Apache 2.0](LICENSE)
