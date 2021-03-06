//
//  AboutViewController.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import "EventDetailViewController.h"
#import "ArtistViewController.h"

@interface EventDetailViewController ()

@end

@implementation EventDetailViewController
@synthesize eventName, linkName, startLabel, startDateTime, endLabel, endDateTime, locationLabel, locationName, venueLabel, venueName, artistsTable, event;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil event:(Event*) newEvent
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Events", @"Events");
		self.event = newEvent;
    }
	
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
	self.eventName.text = self.event.name;
	self.linkName.text = self.event.uri;
	self.startDateTime.text = [self.event.startTime isEqual:nil] ? self.event.startDate : [NSString stringWithFormat:@"%@ - %@", self.event.startDate, self.event.startTime];
	
	if ((self.event.endTime == nil) && (self.event.endDate == nil)) {
		[self.endLabel setHidden:YES];
	}
	else {
		self.startDateTime.text = [self.event.endTime isEqual:nil] ? self.event.startDate : [NSString stringWithFormat:@"%@ - %@", self.event.endDate, self.event.endTime];
	}
	self.endDateTime.text = self.event.endDate;
	self.locationName.text = self.event.city;
	self.venueName.text = self.event.venueName;
}

- (void)viewDidUnload
{
	[self setEventName:nil];
	[self setLinkName:nil];
	[self setStartDateTime:nil];
	[self setStartLabel:nil];
	[self setEndLabel:nil];
	[self setEndDateTime:nil];
	[self setLocationLabel:nil];
	[self setLocationName:nil];
	[self setVenueLabel:nil];
	[self setVenueName:nil];
	[self setArtistsTable:nil];
    [super viewDidUnload];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [[event performers]count] > 0 ? 1 : 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [[event performers]count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	Artist *tmpArtist;
    
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithFrame:CGRectZero];
	}
	
	tmpArtist = [[self.event performers] objectAtIndex:indexPath.row];
	
	cell.textLabel.text = [tmpArtist name];
	if ([tmpArtist mbid] != nil) {
		cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
	}
	else {
		cell.selectionStyle = UITableViewCellSelectionStyleNone;
	}
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	Artist *tmpArtist = [[self.event performers] objectAtIndex:indexPath.row];
	
	if ([tmpArtist mbid] != nil) {
		ArtistViewController *artistView = [[[ArtistViewController alloc] initWithNibName:@"ArtistViewController" bundle:nil] autorelease];
		[artistView viewArtistWithMBID:[tmpArtist mbid]];
		
		[self.navigationController pushViewController:artistView animated:YES];
	}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc {
	[eventName release];
	[linkName release];
	[startDateTime release];
	[startLabel release];
	[endLabel release];
	[endDateTime release];
	[locationLabel release];
	[locationName release];
	[venueLabel release];
	[venueName release];
	[artistsTable release];
	[super dealloc];
}
@end
