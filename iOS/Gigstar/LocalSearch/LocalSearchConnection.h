//
//  LocalSearchConnection.h
//  Gigstar
//
//  Created by Luis Afonso on 4/14/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <Foundation/Foundation.h>

#define GEONAMESURL "http://api.geonames.org/searchJSON?username=meligaletiko"

@protocol LocalSearchConnectionDelegate;

@interface LocalSearchConnection : NSObject {
	
	id <LocalSearchConnectionDelegate> delegate;
}

@property (assign, nonatomic) id <LocalSearchConnectionDelegate> delegate;
@property (retain, nonatomic) NSMutableData *receivedData;

- (id) initWithDelegate: (id)sel;
- (void) performSearchWithQuery:(NSString*) query withMaxRows:(NSInteger)maxRows;
- (void) performSearch: (NSString*)query withMaxRows:(NSInteger)maxRows withCountryCode: (NSString*)countryCode;

@end

@protocol LocalSearchConnectionDelegate

- (void) didFinishReceivingData: (NSDictionary*)results;
- (void) didFailWithError: (NSError*)error;

@end