//
//  AboutViewController.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import "ArtistViewController.h"
#import "ArtistRepository.h"
#import "AppDelegate.h"

@interface ArtistViewController ()

@end

@implementation ArtistViewController
@synthesize nameLabel;
@synthesize pictureImageView;
@synthesize biographyTextView;
@synthesize scrollView;
@synthesize currentArtist;
@synthesize following;
@synthesize artistModel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"Artist", @"Artist");
        
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Follow" style:UIBarButtonItemStylePlain target:self action:@selector(followArtist:)];
        
        scrollView.hidden = YES;
		
    }
    return self;
}

-(void) followArtist: (id)sender
{	
	AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	NSManagedObjectContext *context = [appDelegate managedObjectContext];
	
	if ([self following] == NO) {
		
		Artists *newArtist = (Artists *)[NSEntityDescription insertNewObjectForEntityForName:@"Artists" inManagedObjectContext:context];
		
		[newArtist setMbid: [currentArtist mbid]];
		[newArtist setBiography:[currentArtist biography]];
		[newArtist setName:[currentArtist name]];
		[newArtist setUrl:[currentArtist url]];
		[newArtist setPic_url:[currentArtist picture_url]];
		
		NSError *error = nil;
		if (![context save:&error]) {
			NSLog(@"Error saving artist: %@", error.localizedFailureReason);
		}
		else {
			NSLog(@"Artist Added to DataBase...");
			artistModel = newArtist;
			[self setFollowing:YES];
			[self.navigationItem.rightBarButtonItem setTitle:@"UnFollow"];
		}
	}
	else {
		NSLog(@"Artist should be removed");
		NSLog(@"Artist Selected -> MBID: %@  |  Name: %@  |  URL: %@  |  PIC: %@  |  BIO: %@", [artistModel mbid], [artistModel name], [artistModel url], [artistModel pic_url], [artistModel biography]);
		
		[context deleteObject:artistModel];
	}
	
}

-(Artists*)findArtistInDataBase:(Artist*)search
{
	NSLog(@"findArtistInDataBase");
	
	AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	NSManagedObjectContext *context = [appDelegate managedObjectContext];
	
	NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Artists" inManagedObjectContext:context];
    [fetchRequest setEntity:entity];
	artistModel = nil;
	
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(name = %@) OR (mbid = %@)", search.name, search.mbid];
	[fetchRequest setPredicate:predicate];
	
	NSError *error = nil;
	NSArray *result = [context executeFetchRequest:fetchRequest error:&error];
	
	if (result != nil) {
		artistModel = [result objectAtIndex:0];
	}
	
	return artistModel;
}

-(Artists*)findArtistInDataBaseWithMBID:(NSString*) mbid
{
	NSLog(@"findArtistInDataBaseWithMBID");
	
	AppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	NSManagedObjectContext *context = [appDelegate managedObjectContext];
	artistModel = nil;
	
	NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Artists" inManagedObjectContext:context];
    [fetchRequest setEntity:entity];
	
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"mbid = %@", mbid];
	[fetchRequest setPredicate:predicate];
	
	NSError *error = nil;
	NSArray *result = [context executeFetchRequest:fetchRequest error:&error];
	
	if (result != nil && [result count] > 0) {
		artistModel = [result objectAtIndex:0];
	}
	
	return artistModel;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)viewDidUnload
{
    [self setNameLabel:nil];
    [self setPictureImageView:nil];
    [self setBiographyTextView:nil];
    [self setScrollView:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)viewArtistWithMBID:(NSString *)mbid
{
	NSLog(@"viewArtistWithMBID");
	
	Artists *tmpArtist = [self findArtistInDataBaseWithMBID:mbid];
	if (tmpArtist == nil) {
		
		[self setFollowing:NO];
		
//		ArtistRepository *artistRepository = [[ArtistRepository alloc] init];
//		
//		artistRepository.delegate = self;
//		
//		[artistRepository getArtistWithMBID:mbid];
//		
//		[artistRepository release];
	}
    else {
		
		self.navigationItem.rightBarButtonItem.title = @"UnFollow";
		
		[self setFollowing:YES];
		
//		Artist *artist = [[Artist alloc] init];
//		[artist setMbid:[tmpArtist mbid]];
//		[artist setName:[tmpArtist name]];
//		[artist setBiography:[tmpArtist biography]];
//		[artist setUrl:[tmpArtist url]];
//		[artist setPicture_url:[tmpArtist pic_url]];
//		
//		NSLog(@"Artist: Name: %@ | MBID: %@ | Bio: %@", [artist name], [artist mbid], [artist biography]);
		
//		[self didFinishGettingArtist:artist];
	}
	
	ArtistRepository *artistRepository = [[ArtistRepository alloc] init];
	
	artistRepository.delegate = self;
	
	[artistRepository getArtistWithMBID:mbid];
	
	[artistRepository release];
}

- (void)didFinishGettingArtist:(Artist *)artist
{
    NSLog(@"ArtistViewController didFinishGettingArtist: %@ | ArtistMBID: %@",artist, artist.mbid);
    [artist retain];
	
	currentArtist = artist;
	[currentArtist retain];
    
    nameLabel.text = artist.name;
    if (artist.picture_url != NULL) {
        pictureImageView.image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:artist.picture_url]]];
        pictureImageView.contentMode = UIViewContentModeTopLeft;
    }
    biographyTextView.text  = artist.biography;
    CGRect frame            = biographyTextView.frame;
    frame.size.height       = biographyTextView.contentSize.height;
    biographyTextView.frame = frame;
    
//    CGRect scrollViewFrame      = scrollView.frame;
//    scrollViewFrame.size.height = biographyTextView.contentSize.height + 500;
//    scrollView.frame            = scrollViewFrame;
    CGSize size = CGSizeMake([[UIScreen mainScreen] bounds].size.width, biographyTextView.contentSize.height + biographyTextView.frame.origin.y);
    scrollView.contentSize = size;
//    [scrollView setNeedsDisplay];
    
    NSLog(@"artist picture: %@",artist.picture_url);

    scrollView.hidden = NO;
    
    [artist release];
}

- (void)errorGettingArtist:(NSError *)error
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Sorry" 
                                                    message:@"Could not find details about the artist"
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    
    
    [alert show];
    [alert release];
    
    NSLog(@"Error: %@", error.localizedDescription);
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)dealloc {
	
	[managedObjectContext release];
	
    [nameLabel release];
    [pictureImageView release];
    [biographyTextView release];
    [scrollView release];
    [super dealloc];
}

@end
