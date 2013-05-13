//
//  ArtistFetch.h
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import <Foundation/Foundation.h>

@protocol ArtistFetchDelegate <NSObject>

- (void)didFinishGettingArtist: (NSDictionary *)result;
- (void)errorGettingArtist: (NSError *)error;

@end

@interface ArtistFetch : NSObject <NSURLConnectionDataDelegate>
{
	id <ArtistFetchDelegate> delegate;
}


@property (retain, nonatomic) NSMutableData *receivedData;
@property (retain) id delegate;

- (void)getArtistWithMBID: (NSString *)mbid;

@end