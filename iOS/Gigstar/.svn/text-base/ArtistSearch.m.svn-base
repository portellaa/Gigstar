//
//  ArtistSearch.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt
//

#import "ArtistSearch.h"

@implementation ArtistSearch

@synthesize delegate, receivedData;

- (void)findArtistWithQuery: (NSString *)query withMaxResults:(NSUInteger)maxResults
{
	self.receivedData = [NSMutableData data];
	
	NSString *escapedQuery = [query stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding];
	
	NSString *str = [NSString stringWithFormat:@"http://developer.echonest.com/api/v4/artist/search?api_key=N6E4NIOVYMTHNDM8J&format=json&sort=&sort=familiarity-desc&bucket=id:musicbrainz&bucket=terms&name=%@&results=%u", escapedQuery, maxResults];
	NSURL *url = [NSURL URLWithString:str];
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url];
	[NSURLConnection connectionWithRequest:request delegate:self];
	
	[request release];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
	// connection is starting, clear buffer
	[self.receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
	// data is arriving, add it to the buffer
	[self.receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
	// something went wrong, release connection
	// [connection release]; // not necessary, because we don't retain the connection
	
	[self.delegate errorSearchingArtists:error];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
	// all done, release connection, return results
	
	
	NSError *error = nil;
	
	NSDictionary *jsonDictionary = [NSJSONSerialization JSONObjectWithData:self.receivedData options:NSJSONReadingMutableContainers error: &error];
	if (!jsonDictionary) {
		[self.delegate errorSearchingArtists:error];
	}
	else {
        // check for status in response
        NSString *response_status = [[[jsonDictionary objectForKey:@"response"] objectForKey:@"status"] objectForKey:@"code"];
        
        if ([response_status integerValue] > 0) {
//            NSLog(@"Fetch error: %@", [[[jsonDictionary objectForKey:@"response"] objectForKey:@"status"] objectForKey:@"message"]);
            [self.delegate errorSearchingArtists:nil]; // TODO: maybe should create a new error here?
        }
        else {
//            NSLog(@"Fetch success");
            NSArray *artists = [[jsonDictionary objectForKey:@"response"] objectForKey:@"artists"];
            
            //            for (NSDictionary *artist in artists) {
            //                NSLog(@"%@ - %@",[artist objectForKey:@"id"], [artist objectForKey:@"name"]);
            //            }
            
            [self.delegate didFinishSearchingArtists:artists];
        }
	}
	
	[self.receivedData setLength:0];
}

@end



