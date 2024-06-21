Dart 



import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MapScreen(),
    );
  }
}

class MapScreen extends StatefulWidget {
  @override
  _MapScreenState createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  GoogleMapController? _controller;
  Position? _currentPosition;
  final Set<Marker> _markers = {};

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
  }

  Future<void> _getCurrentLocation() async {
    bool serviceEnabled;
    LocationPermission permission;

    // Test if location services are enabled.
    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return Future.error('Location services are disabled.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return Future.error('Location permissions are denied');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return Future.error(
          'Location permissions are permanently denied, we cannot request permissions.');
    } 

    _currentPosition = await Geolocator.getCurrentPosition();
    setState(() {
      _markers.add(
        Marker(
          markerId: MarkerId('currentLocation'),
          position: LatLng(_currentPosition!.latitude, _currentPosition!.longitude),
        ),
      );
    });

    _controller?.animateCamera(CameraUpdate.newLatLngZoom(
      LatLng(_currentPosition!.latitude, _currentPosition!.longitude), 15));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Google Maps Example'),
      ),
      body: _currentPosition == null
          ? Center(child: CircularProgressIndicator())
          : GoogleMap(
              onMapCreated: (controller) {
                _controller = controller;
              },
              initialCameraPosition: CameraPosition(
                target: LatLng(_currentPosition!.latitude, _currentPosition!.longitude),
                zoom: 15,
              ),
              markers: _markers,
              myLocationEnabled: true,
            ),
    );
  }
}
