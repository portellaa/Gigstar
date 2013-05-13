//
//  LocalSearchConnection.m
//  Gigstar
//
//  Created by Luis Afonso on 4/14/12.
//  Copyright (c) 2012 UA. All rights reserved.
//

#import "LocalSearchConnection.h"

@implementation LocalSearchConnection

@synthesize delegate, receivedData;

- (id) initWithDelegate:(id<LocalSearchConnectionDelegate>)sel
{
	self = [super init];
	
	if (self) {
		[self setDelegate:sel];
	}
	
	return self;
}

- (void) performSearchWithQuery:(NSString*) query withMaxRows:(NSInteger)maxRows
{
	self.receivedData = [NSMutableData data];
	
//	NSString *url = [NSString stringWithFormat:@"http://api.geonames.org/searchJSON?q=%@&maxRows=%d&&username=meligaletiko", [query stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], maxRows];
	
	NSString *url = [NSString stringWithFormat:@"%@&q=%@&maxRows=%d&", [NSString stringWithUTF8String:GEONAMESURL], [query stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], maxRows];

	
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
	[NSURLConnection connectionWithRequest:request delegate:self];
	
	[request release];
	
}

- (void) performSearch: (NSString*)query withMaxRows:(NSInteger)maxRows withCountryCode: (NSString*)countryCode
{
	self.receivedData = [NSMutableData data];
		
//	NSString *url = [NSString stringWithFormat:@"http://api.geonames.org/searchJSON?q=%@&maxRows=%d&username=meligaletiko", [query stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], maxRows];
	
	NSString *url = [NSString stringWithFormat:@"%@&q=%@&maxRows=%d&country=", [NSString stringWithUTF8String:GEONAMESURL], [query stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], maxRows, countryCode];
	
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
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
	
	[self.delegate didFailWithError:error];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
	// all done, release connection, return results
	
	NSError *error = nil;
	
	NSDictionary *parsedData = [NSJSONSerialization JSONObjectWithData:self.receivedData options:NSJSONReadingMutableContainers error: &error];
	if (!parsedData) {
		[self.delegate didFailWithError:error];
	}
	else {
		NSLog(@"%@",parsedData);
		
		[self.delegate didFinishReceivingData:parsedData];
	}
	
	[self.receivedData setLength:0];
}

@end
