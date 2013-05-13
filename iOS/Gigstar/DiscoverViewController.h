//
//  DiscoverViewController.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import <UIKit/UIKit.h>
#import "ArtistRepository.h"

@interface DiscoverViewController : UITableViewController <UISearchDisplayDelegate, UISearchBarDelegate, ArtistRepositoryDelegate>


@property (retain, strong) ArtistRepository *artistRepository;
@property (retain) NSArray *artists;

@end
