//
//  Artists.h
//  Gigstar
//
//  Created by Luis Afonso on 4/19/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Artists : NSManagedObject

@property (nonatomic, retain) NSString * mbid;
@property (nonatomic, retain) NSString * biography;
@property (nonatomic, retain) NSString * url;
@property (nonatomic, retain) NSString * pic_url;
@property (nonatomic, retain) NSString * name;

@end
