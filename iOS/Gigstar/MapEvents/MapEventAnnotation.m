//
//  MapEventAnnotation.m
//  Gigstar
//
//  Created by Luis Afonso on 4/17/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import "MapEventAnnotation.h"

@implementation MapEventAnnotation

@synthesize coordinate, title, subtitle, eventID;

- (id)initWithLocation:(CLLocationCoordinate2D)coord
{
	self = [super init];
	
	if (self) {
		self->coordinate = coord;
	}
	
	return self;
}

- (id)initWithTitle:(NSString*)newTitle withLocation:(CLLocationCoordinate2D)coord withSubTitle:(NSString*)newSubTitle withID:(NSInteger) evID
{
    self = [super init];
    
    if (self) {
        self.title = newTitle;
        self->coordinate = coord;
        self.subtitle = newSubTitle;
		self.eventID = evID;
    }
    
    return self;
}

@end
