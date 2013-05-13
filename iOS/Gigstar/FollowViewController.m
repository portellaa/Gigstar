//
//  FollowViewController.m
//  Gigstar
//
//  Created by Luis Afonso on 4/19/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import "FollowViewController.h"
#import "AppDelegate.h"
#import "ArtistViewController.h"

@interface FollowViewController ()

@end

@implementation FollowViewController

@synthesize followingArtists;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Follow", @"Follow");
		self.tabBarItem.image = [UIImage imageNamed:@"follow.png"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
	AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	NSManagedObjectContext *context = [appDelegate managedObjectContext];
	
	NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Artists" inManagedObjectContext:context];
    [fetchRequest setEntity:entity];
    NSError *error;
    self.followingArtists = [context executeFetchRequest:fetchRequest error:&error];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	NSLog(@"following Artists: %d", [followingArtists count]);
    return [followingArtists count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithFrame:CGRectZero];
	}
	
	Artists *tmpArtist = [followingArtists objectAtIndex:indexPath.row];
	cell.textLabel.text = tmpArtist.name;
	cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	Artists *tmpArtist = [followingArtists objectAtIndex:indexPath.row];
	
    Artist *artist = [[Artist alloc] init];
	[artist setMbid:[tmpArtist mbid]];
	[artist setName:[tmpArtist name]];
	[artist setBiography:[tmpArtist biography]];
	[artist setUrl:[tmpArtist url]];
	[artist setPicture_url:[tmpArtist pic_url]];
	
	ArtistViewController *artistView = [[ArtistViewController alloc] initWithNibName:@"ArtistViewController" bundle:nil];
	[artistView viewArtistWithMBID:[tmpArtist mbid]];
	
	[self.navigationController pushViewController:artistView animated:YES];
	
//	[artistView release];
}

@end
