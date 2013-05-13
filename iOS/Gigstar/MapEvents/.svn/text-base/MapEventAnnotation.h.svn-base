//
//  MapEventAnnotation.h
//  Gigstar
//
//  Created by Luis Afonso on 4/17/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface MapEventAnnotation : NSObject <MKAnnotation>

@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
@property (nonatomic, copy) NSString *title, *subtitle;
@property (readwrite) NSInteger eventID;

- (id)initWithLocation:(CLLocationCoordinate2D)coord;
- (id)initWithTitle:(NSString*)newTitle withLocation:(CLLocationCoordinate2D)coord withSubTitle:(NSString*)newSubTitle withID:(NSInteger) evID;

@end
