//
//  MapEventsViewController.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MKMapView.h>
#import "LocalSearchConnection.h"
#import "MapEventsConnection.h"

@interface MapEventsViewController : UITableViewController <CLLocationManagerDelegate, MKMapViewDelegate, MKAnnotation, UISearchBarDelegate, UISearchDisplayDelegate, LocalSearchConnectionDelegate, MapEventsConnectionDelegate>

@property (retain, nonatomic) IBOutlet MKMapView *myMapView;

@property (strong, nonatomic) CLLocationManager *locationManager;

@property (retain, nonatomic) NSMutableArray *locationsList;
@property (retain, nonatomic) NSArray *geoNames;
@property (retain) NSDictionary *eventsList;

@property (retain, nonatomic) IBOutlet UISearchDisplayController *searchController;

@property (retain) CLLocation *myLocation;
@property (readwrite) MKCoordinateRegion myRegion;

@property (retain) LocalSearchConnection *localSearchConn;
@property (retain) MapEventsConnection *eventsSearchConn;

@end
