//
//  Event.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import "Event.h"

@implementation Event

@synthesize eventID, name, startDate, endDate, performers, startTime, endTime, uri, coordinate, city, venueName;

//-(id)initWithEventID:(NSInteger) evID withName:(NSString*) newName withStartDate:(NSString*) sDate withEndDate:(NSString*) eDate withStartTime:(NSString*) sTime withEndTime:(NSString*) eTime withLat:(NSString*) lati withLong:(NSString*) longi withURI:(NSString*) url

-(id)initWithEventID:(NSInteger) evID withName:(NSString*) newName withStartDate:(NSString*) sDate withEndDate:(NSString*) eDate withStartTime:(NSString*) sTime withEndTime:(NSString*) eTime withCoordinate:(CLLocationCoordinate2D) coord withURI:(NSString*) url withVenueName:(NSString*) venName withCity:(NSString*) cit
{	
	self.eventID = evID;
	self.name = newName;
	self.startDate = sDate;
	self.endDate = eDate;
	self.startTime = sTime;
	self.endTime = eTime;
//	self.latitude = lati;
//	self.longitude = longi;
	self->coordinate = coord;
	self.uri = url;
	self.venueName = venName;
	self.city = cit;
	
	self.performers = [[NSMutableArray alloc] init];
	
	return self;
}

- (void) dealloc {
	
	[self release];
	[super dealloc];
}

@end
