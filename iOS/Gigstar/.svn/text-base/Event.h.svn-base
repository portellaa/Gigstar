//
//  Event.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@interface Event : NSObject {
	
	NSInteger eventID;
	NSString *name, *startDate, *endDate, *startTime, *endTime, *uri;
//	NSString *latitude, *longitude;
	NSString *venueName, *city;
	NSMutableArray *performers;
	CLLocationCoordinate2D coordinate;
	
}

@property (readwrite) NSInteger eventID;
@property (retain) NSString *name, *startDate, *endDate, *startTime, *endTime, *uri;
//@property (retain) NSString *latitude, *longitude;
@property (retain) NSString *venueName, *city;
@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
@property (retain) NSMutableArray *performers;

//-(id)initWithEventID:(NSInteger) evID withName:(NSString*) newName withStartDate:(NSString*) sDate withEndDate:(NSString*) eDate withStartTime:(NSString*) sTime withEndTime:(NSString*) eTime withLat:(NSString*) lati withLong:(NSString*) longi withURI:(NSString*) url;

-(id)initWithEventID:(NSInteger) evID withName:(NSString*) newName withStartDate:(NSString*) sDate withEndDate:(NSString*) eDate withStartTime:(NSString*) sTime withEndTime:(NSString*) eTime withCoordinate:(CLLocationCoordinate2D) coord withURI:(NSString*) url withVenueName:(NSString*) venName withCity:(NSString*) cit;

@end
