//
//  ArtistSearch.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import <Foundation/Foundation.h>

@protocol ArtistSearchDelegate <NSObject>

- (void)didFinishSearchingArtists: (NSArray *)results;
- (void)errorSearchingArtists: (NSError *)error;

@end

@interface ArtistSearch : NSObject <NSURLConnectionDataDelegate>
{
	id <ArtistSearchDelegate> delegate;
}


@property (retain, nonatomic) NSMutableData *receivedData;
@property (retain) id delegate;

- (void)findArtistWithQuery: (NSString *)query withMaxResults:(NSUInteger)maxResults;

@end