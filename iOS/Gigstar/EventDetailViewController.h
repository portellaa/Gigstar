//
//  EventDetailViewController.h
//  Gigstar
//
//  Created by Luis Afonso on 4/19/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Event.h"

@interface EventDetailViewController : UIViewController<UITableViewDelegate>

@property (retain, nonatomic) IBOutlet UILabel *eventName;
@property (retain, nonatomic) IBOutlet UILabel *linkName;
@property (retain, nonatomic) IBOutlet UILabel *startLabel;
@property (retain, nonatomic) IBOutlet UILabel *startDateTime;
@property (retain, nonatomic) IBOutlet UILabel *endLabel;
@property (retain, nonatomic) IBOutlet UILabel *endDateTime;
@property (retain, nonatomic) IBOutlet UILabel *locationLabel;
@property (retain, nonatomic) IBOutlet UILabel *locationName;
@property (retain, nonatomic) IBOutlet UILabel *venueLabel;
@property (retain, nonatomic) IBOutlet UILabel *venueName;
@property (retain, nonatomic) IBOutlet UITableView *artistsTable;

@property (retain) Event *event;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil event:(Event*) newEvent;

@end
