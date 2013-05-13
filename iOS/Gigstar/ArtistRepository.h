//
//  ArtistRepository.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import <Foundation/Foundation.h>
#import "Artist.h"
#import "ArtistSearch.h"
#import "ArtistFetch.h"


@protocol ArtistRepositoryDelegate <NSObject>

@optional

- (void)didFinishSearchingArtists: (NSArray *)results;
- (void)errorSearchingArtists: (NSError *)error;

- (void)didFinishGettingArtist: (Artist *)artist;
- (void)errorGettingArtist: (NSError *)error;

@end


@interface ArtistRepository: NSObject <ArtistSearchDelegate, ArtistFetchDelegate>
{
	id <ArtistRepositoryDelegate> delegate;
}

@property (retain) id delegate;

- (void)findArtistWithQuery: (NSString *)query withMaxResults: (NSUInteger)maxResults;

- (void)getArtistWithMBID: (NSString *)mbid;

@end

