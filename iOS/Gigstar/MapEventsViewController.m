//
//  MapEventsViewController.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import "MapEventsViewController.h"
#import "MapEventAnnotation.h"
#import "MapEventAnnotationView.h"
#import "MapEventAnnotation.h"
#import "EventDetailViewController.h"
#import "Artist.h"

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@implementation MapEventsViewController

@synthesize myMapView, locationManager, myLocation, localSearchConn, searchController, geoNames, locationsList, eventsList, coordinate, eventsSearchConn, myRegion;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
		self.title = NSLocalizedString(@"Map", @"Map");
		self.tabBarItem.image = [UIImage imageNamed:@"map.png"];
		
		self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"location.png"] style:UIBarButtonItemStylePlain target:self action:@selector(goToMyPosition:)];
		
		[self.myMapView setDelegate:self];
		
		locationManager = [[CLLocationManager alloc] init];
		[self.locationManager setDelegate:self];
		
		self.eventsSearchConn = [[MapEventsConnection alloc] initWithDelegate:self];
    }
	
    return self;
}

- (void) initTableStyle
{
    [self.searchDisplayController.searchResultsTableView setSeparatorColor:UIColorFromRGB(0x555555)];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	[locationManager startUpdatingLocation];
	[locationManager setDistanceFilter:kCLDistanceFilterNone];
	[locationManager setDesiredAccuracy:kCLLocationAccuracyBest];
	
    [self.myMapView setShowsUserLocation:YES];
	[self.myMapView setUserTrackingMode:MKUserTrackingModeFollowWithHeading animated: YES];
}

- (void)viewDidUnload
{
	
	[locationManager stopUpdatingLocation];
	
	[self setSearchController:nil];
	[self setMyMapView:nil];
	[super viewDidUnload];
	
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{

	if ((newLocation.coordinate.latitude != myLocation.coordinate.latitude) || (newLocation.coordinate.longitude != myLocation.coordinate.longitude)) {
		[myMapView setRegion: MKCoordinateRegionMake([newLocation coordinate], MKCoordinateSpanMake(0.10, 0.10)) animated:YES];
		self.myLocation = newLocation;
		NSLog(@"Different position: new: %@ - old: %@", newLocation, myLocation);
		
//		NSLog(@"Starting search for events to the location: Lat: %@ and Long: %@", [NSString stringWithFormat:@"%f", newLocation.coordinate.latitude], [NSString stringWithFormat:@"%f", newLocation.coordinate.latitude]);
//		[eventsSearchConn performSearchWithLat:[NSString stringWithFormat:@"%f", newLocation.coordinate.latitude] withLong:[NSString stringWithFormat:@"%f", newLocation.coordinate.longitude]];
	}
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
	
	if (error.code == kCLErrorDenied) {
		[locationManager stopUpdatingLocation];
	}
	else if (error.code == kCLErrorLocationUnknown) {
		NSLog(@"Retrying update location...");
	}
	else {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error retrieving location" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
		[alert release];
	}
}

- (void) goToMyPosition: (id)sender
{
	[myMapView setRegion: MKCoordinateRegionMake([myLocation coordinate], MKCoordinateSpanMake(0.10, 0.10)) animated:YES];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
	[self.searchDisplayController.searchResultsTableView setHidden:NO];
    
    [self.searchDisplayController.searchResultsTableView setBackgroundColor:[UIColor grayColor]];

	[localSearchConn performSearchWithQuery:searchBar.text withMaxRows:20];
}

- (void)searchDisplayControllerDidBeginSearch:(UISearchDisplayController *)controller
{
	NSLog(@"Starting Search...");
	
	localSearchConn = [[LocalSearchConnection alloc] initWithDelegate:self];
	locationsList = [[NSMutableArray alloc] init];
	NSLog(@"Initiated google connection...");

}

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString
{
	controller.searchResultsTableView.hidden = YES;
	
	return NO;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [locationsList count] > 0 ? 1 : 0;
//	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	NSLog(@"numberOfRowsInSection: %d", [locationsList count]);
	return [locationsList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
		cell.textLabel.textColor       = UIColorFromRGB(0xFFFFFF);
        cell.detailTextLabel.textColor = UIColorFromRGB(0xD0D0D0);
        cell.selectionStyle            = UITableViewCellSelectionStyleNone;
	}
	
	NSLog(@"cellForRowAtIndexPath: %@", [locationsList objectAtIndex:indexPath.row]);
	cell.textLabel.text = [locationsList objectAtIndex:indexPath.row];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"Selected Row: %d", indexPath.row);
	
	[searchController setActive:NO animated:YES];
	
	NSDictionary *tmpGeoLocation = [geoNames objectAtIndex:indexPath.row];
	
	NSLog(@"%@ - %@", [tmpGeoLocation valueForKey:@"lat"], [tmpGeoLocation valueForKey:@"lng"]);
	
	CLLocationCoordinate2D moveToLocation = CLLocationCoordinate2DMake([[tmpGeoLocation valueForKey:@"lat"] doubleValue], [[tmpGeoLocation valueForKey:@"lng"] doubleValue]);
	
	[myMapView setRegion: MKCoordinateRegionMake(moveToLocation, MKCoordinateSpanMake(0.10, 0.10)) animated:YES];	
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation
{
	NSLog(@"viewForAnnotation");
	
	if ([annotation isKindOfClass:[MKUserLocation class]])
		return nil;
	
	MKAnnotationView* tmpAnnotView = nil;
	
	static NSString* ident = @"EventsAnnotation";
	tmpAnnotView = [mapView dequeueReusableAnnotationViewWithIdentifier:ident];

	if (tmpAnnotView == nil) {
		NSLog(@"Allocating new MapEventAnnotationView");
		tmpAnnotView = [[[MapEventAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:ident] autorelease];

	} else {
		tmpAnnotView.annotation = annotation;
	}
	
	UIButton *eventDetailsBtn = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
	[eventDetailsBtn addTarget:self action:@selector(openEventView:) forControlEvents:UIControlEventTouchUpInside];
	[eventDetailsBtn setTag:[((MapEventAnnotation*)annotation) eventID]];
	
	tmpAnnotView.rightCalloutAccessoryView = eventDetailsBtn;
	tmpAnnotView.enabled = YES;
	tmpAnnotView.canShowCallout = YES;
	
	return tmpAnnotView;
	
}

- (void)openEventView:(id) sender {
	
	UIButton* tmp = sender;
	
	NSLog(@"Event Tag: %d", [tmp tag]);
	
	for (Event *tmpEvent in [self.eventsList allValues]) {
		if (tmpEvent.eventID == [tmp tag]) {
			NSLog(@"Event -> ID: %d | Name: %@ | startDate: %@ | endDate: %@ | startTime: %@ | endTime: %@ | Latitude: %f | Longitude: %f", [tmpEvent eventID], [tmpEvent name], [tmpEvent startDate], [tmpEvent endDate], [tmpEvent startTime], [tmpEvent endTime], tmpEvent.coordinate.latitude, tmpEvent.coordinate.longitude);
			
			EventDetailViewController *eventDetail = [[[EventDetailViewController alloc] initWithNibName:@"EventDetailViewController" bundle:nil event:tmpEvent] autorelease];
			
			[self.navigationController pushViewController:eventDetail animated:YES];
			
			break;
		}
	}
}

- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
	NSLog(@"didUpdateUserLocation");
}

- (void)mapView:(MKMapView *)mapView regionWillChangeAnimated:(BOOL)animated
{
	NSLog(@"regionWillChangeAnimated");
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated
{
	
	//TODO Optimize search to the API
	NSLog(@"regionDidChangeAnimated");
	NSLog(@"Visible Region: %f -- %f", myRegion.center.latitude, mapView.region.center.latitude);
	
	if ((myRegion.center.latitude != mapView.region.center.latitude) || (myRegion.center.longitude != mapView.region.center.longitude)) {
		
		myRegion = [mapView region];
	
		NSLog(@"Starting search for events to the location: Lat: %@ and Long: %@", [NSString stringWithFormat:@"%f", mapView.centerCoordinate.latitude], [NSString stringWithFormat:@"%f", mapView.centerCoordinate.latitude]);
		[eventsSearchConn performSearchWithLat:[NSString stringWithFormat:@"%f", mapView.centerCoordinate.latitude] withLong:[NSString stringWithFormat:@"%f", mapView.centerCoordinate.longitude]];
	}
}


// Our Protocol Delegates
- (void)didFinishReceivingData:(NSDictionary *)results
{
	NSLog(@"Received Results: %@", results);
	geoNames = [results valueForKey:@"geonames"];
	NSLog(@"Received Geonames: %@", geoNames);
	
	NSLog(@"Copying geonames to the location list -- Size of geonames: %d ...", [geoNames count]);
	for(NSDictionary *item in geoNames) {
		[locationsList addObject: [NSString stringWithFormat:@"%@, %@, %@", [item valueForKey:@"name"], [item valueForKey:@"adminName1"], [item valueForKey:@"countryName"]]];
		NSLog(@"Adding || %@, %@, %@ || to the list", [item valueForKey:@"name"], [item valueForKey:@"adminName1"], [item valueForKey:@"countryName"]);
	}
	
	[locationsList retain];
	
	NSLog(@"Checking if results have been returned and reloading the tableview -- Size of locationList: %d ...", [locationsList count]);
	[[[self searchDisplayController] searchResultsTableView] reloadData];
	if ([locationsList count] > 0) {
		NSLog(@"Reloading Data to the table view. Data LocatinonsList: %@", locationsList);
		[[[self searchDisplayController] searchResultsTableView] reloadData];
	}
	
	[geoNames retain];
	
}

- (void)didFinishReceivingDataEvents:(NSDictionary *)results
{
	NSLog(@"%@", results);
	
	for (Event *tmpEvent in [results allValues]) {
		
		NSLog(@"Event -> ID: %d | Name: %@ | startDate: %@ | endDate: %@ | startTime: %@ | endTime: %@ | Latitude: %f | Longitude: %f", [tmpEvent eventID], [tmpEvent name], [tmpEvent startDate], [tmpEvent endDate], [tmpEvent startTime], [tmpEvent endTime], tmpEvent.coordinate.latitude, tmpEvent.coordinate.longitude);
		
		MapEventAnnotation *tmpAnnot = [[MapEventAnnotation alloc] initWithTitle:[tmpEvent name] 
																	withLocation:[tmpEvent coordinate]
																	withSubTitle:([tmpEvent endDate] == nil) ? [NSString stringWithFormat:@"%@", [tmpEvent startDate]] : [NSString stringWithFormat:@"%@ - %@", [tmpEvent startDate], [tmpEvent endDate]] 
																		  withID:[tmpEvent eventID]];		

		NSLog(@"Artists counter: %d", [[tmpEvent performers] count]);
		for (Artist *tmpArtist in [tmpEvent performers]) {
			NSLog(@"Artist ID: %@  |  Name: %@  |  URL: %@", [tmpArtist mbid], [tmpArtist name], [tmpArtist url]);
		}
		
		[self.myMapView addAnnotation:tmpAnnot];
		
		[tmpAnnot release];
	}
	
	self.eventsList = results;
	
	NSLog(@"EventsListCount: %d", [self.eventsList count]);
}

- (void) didFailWithError: (NSError*)error
{
	NSLog(@"Erro: %@", error.localizedDescription);
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc {
	
	[eventsSearchConn release];
	[localSearchConn release];
	[locationsList release];
	[geoNames release];
	
	[locationManager release];
    [myMapView release];
	[searchController release];
	[myMapView release];
    [super dealloc];
}

@end
