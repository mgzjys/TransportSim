object Models {

import scala.util.Random
import scala.collection.mutable.ListBuffer

  class Distance (distance: Double) extends Serializable {
    def asFeet = distance
    def asMiles = distance * 0.0001893939394
    def asMeters = distance * 0.3048
    def asKilometers = distance * 0.0003048
  }

  case class Point(lat: BigDecimal, lon: BigDecimal) extends Serializable // OSM: Node

  class Person (r: List[Point]) extends Serializable { // basic agent
    val route = r
    val width = new Distance (1.5 + Random.nextDouble)
    val length = new Distance (0.5 + Random.nextDouble)
    var arrived = false
    var crashed = false
    var travelTime: Int = 0
    var currentRouteSegment = 0
    var centroid = route{0}
    var vehicle = None: Option[Vehicle]
    def view {
      print("{\"type\":\"person\", \"lat\":\"" + centroid.lat + "\", \"lon\":\"" + centroid.lon + "\", \"width\":\"" + width.asFeet + "\", \"length\":\"" + length.asFeet + "\", \"arrived\":\"" + arrived + "\"}");
    }
    def transport {
      if (arrived == false) {
	travelTime = travelTime + 1
	currentRouteSegment = currentRouteSegment + 1
	centroid = route{currentRouteSegment}
	vehicle match {
	  case Some(vehicle) =>
	    vehicle.centroid = centroid
	  case _ => false
	}
	if (currentRouteSegment == route.length - 1) {
	  arrived = true
	}
      }
    }
  }

  abstract class Vehicle (c: Point)  extends Serializable {
    var driver = None: Option[Person]
    val subtype: String
    var passengers = ListBuffer[Person]()
    val maxPassengers: Int
    val width: Distance
    val length: Distance
    var crashed = false
    var centroid = c
    def view {
      print("{\"type\":\"Vehicle\", \"subtype\":\"" + this.subtype + "\", \"lat\":\"" + centroid.lat + "\", \"lon\":\"" + centroid.lon + "\", \"width\":\"" + width.asFeet + "\", \"length\":\"" + length.asFeet + "\"}");
    }
  }

  class Bicycle extends Vehicle {
    val subtype = "Bicycle"
    val maxPassengers = 1
    val width = new Distance (2.0 + Random.nextDouble)
    val length = new Distance (6.0 + Random.nextDouble)
  }

  class Car extends Vehicle {
    val subtype = "Car"
    val maxPassengers = 4
    val width = new Distance (6.0 + Random.nextDouble)
    val length = new Distance (12.0 + Random.nextDouble)
  }

  class Bus extends Vehicle {
    val subtype = "Bus"
    val maxPassengers = 84
    val width = new Distance (8.0)
    val length = new Distance (40.0)
  }

  abstract class LightRail extends Vehicle {
    val subtype = "LightRail"
    val maxPassengers = 220
  }

  abstract class HeavyRail extends Vehicle {
    val subtype = "HeavyRail"
    val maxPassengers = 800
  }

  class Track extends Serializable {
    val width = 4.708
  }

  abstract class Railway {
    val tracks: List[Track]
  }

  abstract class Lane extends Serializable {
    val width: Double
  }

  class GeneralLane extends Lane { 
    val width = 10.0
  }

  class FreewayLane extends Lane { 
    val width = 13.0
  }

  class ParkingLane extends Lane { 
    val width = 10.0
  }

  class BikeLane extends Lane { 
    val width = 5.0
  }

  class BusLane extends Lane { 
    val width = 10.0
  }

  class Sidewalk extends Lane { 
    val width = 4.0
  }

  abstract class HalfRoad extends Serializable { // OSM: Way w/ oneway != yes
    val lanes = List[Lane]()
    val parkingLane: ParkingLane
    val bikeLane: BikeLane
    val busLane: BusLane
    val sidewalk: Sidewalk
  }

  abstract class TwoWayRoad extends Serializable { // OSM: Way w/ oneway = yes
    val wayOne: HalfRoad
    val wayTwo: HalfRoad
    val centerLine: LineString
  }

  abstract class OneWayRoad extends Serializable { // OSM: lanes (only sometimes)
    val wayOne: HalfRoad
    val centerLine: LineString
  }

  class OneWayFreeway extends Serializable {
    val freewayLanes = List[Lane]()
  }

  abstract class Freeway extends Serializable {
    val wayOne: OneWayFreeway
    val wayTwo: OneWayFreeway
  }

  class ParkingSpace (c: Point) extends Serializable {
    val width = 8.0
    val length = 16.0
    val centroid = c
    var occupant = None: Option[Car]
    def view {
      print("{\"type\"=\"parkingSpace\", \"lat\":\"" + centroid.lat + "\", lon:\"" + centroid.lon + "\"}");
    }
  }

}
