//
//  MapEventsConnection.h
//  Gigstar
//
//  Created by Luis Afonso on 4/18/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <Foundation/Foundation.h>

#define SONGKICKURL "http://api.songkick.com/api/3.0/events.json?apikey=p6G0ajVMJ65yAMJP&location=geo:"

@protocol MapEventsConnectionDelegate;

@interface MapEventsConnection : NSObject
{
	id<MapEventsConnectionDelegate> delegate;
}

@property(retain) id<MapEventsConnectionDelegate> delegate;
@property (retain, nonatomic) NSMutableData *receivedData;

- (id) initWithDelegate: (id)sel;
- (void) performSearchWithLat:(NSString*) latitude withLong:(NSString*) longitude;

@end

@protocol MapEventsConnectionDelegate

- (void) didFinishReceivingDataEvents: (NSDictionary*)results;
- (void) didFailWithError: (NSError*)error;

@end
