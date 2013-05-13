//
//  MapEventAnnotationView.m
//  Gigstar
//
//  Created by Luis Afonso on 4/17/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import "MapEventAnnotationView.h"
#import "MapEventAnnotation.h"

@implementation MapEventAnnotationView

- (id)initWithAnnotation:(id<MKAnnotation>)annotation reuseIdentifier:(NSString *)reuseIdentifier
{
	self = [super initWithAnnotation: annotation reuseIdentifier:reuseIdentifier];
	
	return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
