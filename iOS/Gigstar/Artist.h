//
//  Artist.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import <Foundation/Foundation.h>

@interface Artist : NSObject

@property (retain) NSString *mbid,
                            *name,
                            *biography,
                            *url,
                            *picture_url;

@property (retain) NSArray *tags, *similar;


@end
