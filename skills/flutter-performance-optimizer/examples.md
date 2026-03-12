# Performance Optimization Examples

## 1. BlocBuilder Scope - Wrap Only Changing Parts

```dart
// ❌ Entire scaffold rebuilds
BlocBuilder<MyBloc, MyState>(
  builder: (context, state) => Scaffold(
    appBar: AppBar(title: const Text('Title')),
    body: Column([const StaticHeader(), Text(state.data)]),
  ),
)

// ✅ Only changing part rebuilds
Scaffold(
  appBar: AppBar(title: const Text('Title')),
  body: Column([
    const StaticHeader(),
    BlocBuilder<MyBloc, MyState>(
      builder: (context, state) => Text(state.data),
    ),
  ]),
)
```

## 2. Lists - Use ListView.builder

```dart
// ❌ Builds all items at once
SingleChildScrollView(
  child: Column(
    children: List.generate(1000, (i) => ItemWidget(i)),
  ),
)

// ✅ Lazy loading
ListView.builder(
  itemCount: 1000,
  itemBuilder: (context, index) => ItemWidget(index),
)
```

## 3. Stream/Future - Create Once

```dart
// ❌ Creates new stream every build
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: fetchData(),  // New stream every build!
      builder: (context, snapshot) => Text(snapshot.data ?? ''),
    );
  }
}

// ✅ Create once in initState
class MyWidget extends StatefulWidget {
  @override
  State<MyWidget> createState() => _MyWidgetState();
}

class _MyWidgetState extends State<MyWidget> {
  late final Stream<String> _stream;
  
  @override
  void initState() {
    super.initState();
    _stream = fetchData();
  }
  
  @override
  Widget build(BuildContext context) {
    return StreamBuilder(stream: _stream, builder: ...);
  }
}
```

## 4. Controllers - Always Dispose

```dart
// ❌ Memory leak
class _MyWidgetState extends State<MyWidget> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  
  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: Duration(seconds: 1));
  }
  // Missing dispose!
}

// ✅ Proper cleanup
class _MyWidgetState extends State<MyWidget> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  
  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: Duration(seconds: 1));
  }
  
  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
```
