//
//  DiscoverViewController.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import "DiscoverViewController.h"
#import "ArtistViewController.h"
#import "ArtistRepository.h"
#import "Artist.h"

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface DiscoverViewController ()

@end

//UIColor *searchResultBgColor;

@implementation DiscoverViewController

@synthesize artistRepository;
@synthesize artists;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil 
{
	self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Discover", @"Discover");
		self.tabBarItem.image = [UIImage imageNamed:@"discover.png"];
		
		artistRepository = [[ArtistRepository alloc] init];
		[artistRepository setDelegate:self];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
//    searchResultBgColor = [self.searchDisplayController.searchResultsTableView.backgroundColor retain];
	
    [self initTableStyle];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
	
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void) initTableStyle
{
    self.searchDisplayController.searchResultsTableView.separatorColor = UIColorFromRGB(0x555555);
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
//    NSLog(@"numberOfSectionsInTableView");
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
//    NSLog(@"numberOfRowsInSection: %u",[artists count]);
    
    return [artists count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSLog(@"cellForRowAtIndexPath %u", indexPath.row);
    static NSString *CellIdentifier = @"CellWithSubtitle";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
        cell.textLabel.textColor       = UIColorFromRGB(0xFFFFFF);
        cell.detailTextLabel.textColor = UIColorFromRGB(0xD0D0D0);
        cell.selectionStyle            = UITableViewCellSelectionStyleNone;
	}
	
    Artist *artist = [artists objectAtIndex:(NSUInteger)indexPath.row];
    
    cell.textLabel.text = [[NSString alloc] initWithString:artist.name];
    
//    UIImage *cellImage = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:@"http://m3.licdn.com/mpr/mpr/shrink_80_80/p/3/000/09a/18c/34bb02d.jpg"]]];
//    cell.imageView.image = cellImage;
    
    NSMutableArray *tags = [[NSMutableArray alloc] init];
    
    for (NSUInteger i = 0;
         i < 3 && [artist.tags count] > i;
         i++
    ) {
        [tags addObject:[artist.tags objectAtIndex:i]];
    }

    cell.detailTextLabel.text = [[tags componentsJoinedByString:@", "] capitalizedString];
    
    [tags release];
    
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

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
   // Navigation logic may go here. Create and push another view controller.
    
    ArtistViewController *artistViewController = [[ArtistViewController alloc] initWithNibName:@"ArtistViewController" bundle:nil];
    // ...
    // Pass the selected object to the new view controller.
    [self.navigationController pushViewController:artistViewController animated:YES];
    Artist *artist = [artists objectAtIndex:indexPath.row];
    [artistViewController viewArtistWithMBID:artist.mbid];
    [artistViewController release];
}


- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString
{
//	NSLog(@"Typed %@",controller.searchBar.text);
//    [controller.searchResultsTableView setBackgroundColor:searchResultBgColor];
    controller.searchResultsTableView.hidden = TRUE;
    

    return NO;
}

// Called when user press the search button, calling the webservice to retrieve the results
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
	NSLog(@"Searched: %@", searchBar.text);
    
	self.searchDisplayController.searchResultsTableView.hidden = FALSE;
    
	[self.searchDisplayController.searchResultsTableView setBackgroundColor:[UIColor colorWithWhite:1 alpha:0]];
    
	[artistRepository findArtistWithQuery:searchBar.text withMaxResults:100];
}


- (void)searchDisplayControllerDidBeginSearch:(UISearchDisplayController *)controller
{
//	NSLog(@"Entered");
}

- (void)searchDisplayControllerDidEndSearch:(UISearchDisplayController *)controller
{
//	NSLog(@"Exited");
}


// Called when the protocol receives data
- (void)didFinishSearchingArtists:(NSArray *)results
{
	
	NSLog(@"DiscoverViewController didFinishSearchingArtists. Total results: %u",[results count]);
    
    [artists release];
    
    artists = results;
    
    [artists retain];
    
//    for (NSDictionary *artist in artists) {
//        NSLog(@"%@ - %@",[artist objectForKey:@"id"], [artist objectForKey:@"name"]);
//    }
    
    [[self.searchDisplayController searchResultsTableView] reloadData];
}

- (void)errorSearchingArtists:(NSError *)error
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Sorry" 
                                                    message:@"An unexpected error occurred"
                                                   delegate:nil 
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
    [alert release];
	NSLog(@"Error: %@", error.localizedDescription);
}


@end
