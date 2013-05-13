//
//  MapEventsConnection.m
//  Gigstar
//
//  Created by Luis Afonso on 4/18/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import "MapEventsConnection.h"
#import "Event.h"
#import "Artist.h"

@implementation MapEventsConnection

@synthesize delegate, receivedData;

- (id) initWithDelegate: (id)sel
{
	self = [super init];
	
	if (self)
		[self setDelegate:sel];

	return self;
}

- (void) performSearchWithLat:(NSString*) latitude withLong:(NSString*) longitude
{
	
	self.receivedData = [NSMutableData data];
	
	NSString *url = [NSString stringWithFormat:@"%@%@,%@", [NSString stringWithUTF8String:SONGKICKURL], [latitude stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], [longitude stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding]];
	
	NSLog(@"MapEventsConnection URL: %@", url);
	
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
	[NSURLConnection connectionWithRequest:request delegate:self];
	
	[request release];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
	// connection is starting, clear buffer
	[self.receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
	// data is arriving, add it to the buffer
	[self.receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{	
	[self.delegate didFailWithError:error];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
	
	NSError *error = nil;
	NSMutableDictionary *eventsFinal = nil;
	
	NSDictionary *parsedData = [NSJSONSerialization JSONObjectWithData:self.receivedData options:NSJSONReadingMutableContainers error: &error];
	if (!parsedData) {
		[self.delegate didFailWithError:error];
	}
	else {
		NSLog(@"%@",parsedData);
		
		NSDictionary *resultsPage;
		Artist *tmpArtist;
		NSArray *tmpMbids;

		Event *tmpEvent;
		
		if ((resultsPage = [parsedData valueForKey:@"resultsPage"]) != NULL) {
			
			eventsFinal = [[NSMutableDictionary alloc] init];
			
			for (NSDictionary *tmpValue in [[resultsPage valueForKey:@"results"] valueForKey:@"event"]) {
				
				if ([tmpValue valueForKey:@"location"] == nil) {
					
					NSLog(@"Event --> ID: %@ | Latitude: %@ | Longitude: %@", [tmpValue valueForKey:@"id"], [[tmpValue valueForKey:@"venue"] valueForKey:@"lat"], [[tmpValue valueForKey:@"venue"] valueForKey:@"lng"]);
					
					tmpEvent = [[Event alloc] initWithEventID:[[tmpValue valueForKey:@"id"] intValue]
													 withName:[tmpValue valueForKey:@"displayName"] 
												withStartDate:[[tmpValue valueForKey:@"start"] valueForKey:@"date"] 
												  withEndDate:[[tmpValue valueForKey:@"end"] valueForKey:@"date"] 
												withStartTime:[[tmpValue valueForKey:@"start"] valueForKey:@"time"] 
												  withEndTime:[[tmpValue valueForKey:@"end"] valueForKey:@"time"]
											   withCoordinate:CLLocationCoordinate2DMake([[[tmpValue valueForKey:@"venue"] valueForKey:@"lat"]doubleValue], [[[tmpValue valueForKey:@"venue"] valueForKey:@"lng"] doubleValue])
													  withURI:[tmpValue valueForKey:@"uri"] 
												withVenueName:[[tmpValue valueForKey:@"venue"] valueForKey:@"displayName"] 
													 withCity:[NSString stringWithFormat:@"%@, %@, %@", 
															   [[[tmpValue valueForKey:@"venue"] valueForKey:@"metroArea"] valueForKey:@"displayName"],
															   [[[[tmpValue valueForKey:@"venue"] valueForKey:@"metroArea"] valueForKey:@"state"] valueForKey:@"displayName"],
															   [[[[tmpValue valueForKey:@"venue"] valueForKey:@"metroArea"] valueForKey:@"country"] valueForKey:@"displayName"]]];
					
					for (NSDictionary *artist in [tmpValue valueForKey:@"performance"]) {
						NSLog(@"MBID: %@  |  Name: %@", [[[artist valueForKey:@"artist"] valueForKey:@"identifier"] valueForKey:@"mbid"], [artist valueForKey:@"displayName"]);
						
						tmpArtist = [[Artist alloc] init];
						tmpMbids = [[[artist valueForKey:@"artist"] valueForKey:@"identifier"] valueForKey:@"mbid"];
						[tmpArtist setMbid:(tmpMbids.count > 0 ? [tmpMbids objectAtIndex:0] : nil)];
						[tmpArtist setName:[[artist valueForKey:@"artist"] valueForKey:@"displayName"]];
						
						[[tmpEvent performers] addObject:tmpArtist];
						
					}
					
					[eventsFinal setObject:tmpEvent forKey:[tmpValue valueForKey:@"id"]];
					
				}
				else {
					
					NSLog(@"Event --> ID: %@ | Latitude: %@ | Longitude: %@", [tmpValue valueForKey:@"id"], [[tmpValue valueForKey:@"location"] valueForKey:@"lat"], [[tmpValue valueForKey:@"location"] valueForKey:@"lng"]);
					
					tmpEvent = [[Event alloc] initWithEventID:[[tmpValue valueForKey:@"id"] intValue]
													 withName:[tmpValue valueForKey:@"displayName"] 
												withStartDate:[[tmpValue valueForKey:@"start"] valueForKey:@"date"] 
												  withEndDate:[[tmpValue valueForKey:@"end"] valueForKey:@"date"] 
												withStartTime:[[tmpValue valueForKey:@"start"] valueForKey:@"time"] 
												  withEndTime:[[tmpValue valueForKey:@"end"] valueForKey:@"time"]
											   withCoordinate:CLLocationCoordinate2DMake([[[tmpValue valueForKey:@"location"] valueForKey:@"lat"]doubleValue], [[[tmpValue valueForKey:@"location"] valueForKey:@"lng"] doubleValue])
													  withURI:[tmpValue valueForKey:@"uri"] 
												withVenueName:[[tmpValue valueForKey:@"venue"] valueForKey:@"displayName"] 
													 withCity:[[tmpValue valueForKey:@"location"] valueForKey:@"city"]];
					
					[eventsFinal setObject:tmpEvent forKey:[tmpValue valueForKey:@"id"]];
					
					for (NSDictionary *artist in [tmpValue valueForKey:@"performance"]) {
						NSLog(@"MBID: %@  |  Name: %@", [[[artist valueForKey:@"artist"] valueForKey:@"identifier"] valueForKey:@"mbid"], [artist valueForKey:@"displayName"]);
						
						tmpArtist = [[Artist alloc] init];
//						tmpMbids = [[[artist valueForKey:@"artist"] valueForKey:@"identifier"] valueForKey:@"mbid"];
						tmpMbids = [[artist valueForKey:@"artist"] valueForKey:@"identifier"];
//						[tmpArtist setMbid:(tmpMbids.count > 0 ? [tmpMbids objectAtIndex:0] : nil)];
						[tmpArtist setMbid:(tmpMbids.count > 0 ? [((NSDictionary*)[tmpMbids objectAtIndex:0]) valueForKey:@"mbid"] : nil)];
						[tmpArtist setName:[[artist valueForKey:@"artist"] valueForKey:@"displayName"]];
						[tmpArtist setUrl:([tmpMbids count] > 0 ? [((NSDictionary*)[tmpMbids objectAtIndex:0]) valueForKey:@"href"] : [[artist valueForKey:@"artist"] valueForKey:@"uri"])];
						
						[[tmpEvent performers] addObject:tmpArtist];
						
					}
					
					[eventsFinal setObject:tmpEvent forKey:[tmpValue valueForKey:@"id"]];
				}
			}
		}
		
		[tmpEvent release];
		
		[self.delegate didFinishReceivingDataEvents:eventsFinal];
	}
	
	[self.receivedData setLength:0];
}

@end
