//
//  ArtistViewController.h
//  Gigstar
//
//  Created by Marco Oliveira on 04/16/2012.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import "ArtistRepository.h"
#import "Artists.h"
#import "Artist.h"

@interface ArtistViewController : UIViewController <UIApplicationDelegate, ArtistRepositoryDelegate, UIAlertViewDelegate>
{
	NSManagedObjectContext *managedObjectContext;
}

- (void)viewArtistWithMBID: (NSString *)mbid;

@property (retain, nonatomic) IBOutlet UILabel *nameLabel;
@property (retain, nonatomic) IBOutlet UIImageView *pictureImageView;
@property (retain, nonatomic) IBOutlet UITextView *biographyTextView;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) Artist *currentArtist;
@property (retain, nonatomic) Artists *artistModel;

//@property (nonatomic, retain) NSManagedObjectContext *managedObjectContext;
@property (atomic) BOOL following;



@end
