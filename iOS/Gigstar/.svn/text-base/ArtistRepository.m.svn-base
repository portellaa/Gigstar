//
//  ArtistRepository.m
//  Gigstar
//
//  Created by
//	Luis Portela, luis.portela@ua.pt
//  Marco Oliveira, marcooliveira@ua.pt

#import "ArtistRepository.h"
#import <Foundation/Foundation.h>
#import "ArtistSearch.h"
#import "Artist.h"

@implementation ArtistRepository

@synthesize delegate;

// ARTIST SEARCH
- (void)findArtistWithQuery: (NSString *)query withMaxResults:(NSUInteger)maxResults
{
    ArtistSearch *search = [[ArtistSearch alloc] init];
    
    search.delegate = self;
    
    [search findArtistWithQuery:query withMaxResults:maxResults];
    
    [search release];
}

- (void)didFinishSearchingArtists:(NSArray *)results
{
    NSLog(@"ArtistRepository didFinishSearchingArtists");

    if ([self.delegate respondsToSelector:@selector(didFinishSearchingArtists:)]) {
        // normalize results
        NSMutableArray *normalizedResults = [[NSMutableArray alloc] init];
        
        NSLog(@"iterating results");
        
        for (NSDictionary *artistDict in results) {
            NSString *mbid = [[[[[artistDict objectForKey:@"foreign_ids"] objectAtIndex:0] objectForKey:@"foreign_id"] componentsSeparatedByString:@":"] objectAtIndex:2];
            
            // skip artists that do not have MBID
            if (mbid == NULL) {
                continue;
            }
            
            NSLog(@"allocated artist");
            Artist *artist = [[Artist alloc] init];

            artist.name = [artistDict objectForKey:@"name"];
            artist.mbid = mbid;
            

            NSMutableArray *tags = [[NSMutableArray alloc] init];
            
            for (NSDictionary *term in [artistDict objectForKey:@"terms"]) {
                [tags addObject:[term objectForKey:@"name"]];
            }
            
            artist.tags = tags;
            
            [normalizedResults addObject:artist];
            
            [artist release];
        }

        for (Artist *artist in normalizedResults) {
            NSLog(@"artist name: %@, mbid: %@",artist.name,artist.mbid);
        }
        
        NSLog(@"ArtistRepository calling didFinishSearchingArtists");
        [self.delegate didFinishSearchingArtists:normalizedResults];
        
        [normalizedResults release];
    }
}

- (void)errorSearchingArtists:(NSError *)error
{
    NSLog(@"ArtistRepository errorSearchingArtists");
    if ([self.delegate respondsToSelector:@selector(errorSearchingArtists:)]) {
        [self.delegate errorSearchingArtists:error];
    }
}

// ARTIST FETCH
- (void)getArtistWithMBID:(NSString *)mbid
{
    ArtistFetch *fetch = [[ArtistFetch alloc] init];
    
    fetch.delegate = self;
    
    [fetch getArtistWithMBID:mbid];
    
    [fetch release];
}

- (void)didFinishGettingArtist:(NSDictionary *)result
{
    NSLog(@"ArtistRepository didFinishFetchingArtist");
    
    if ([self.delegate respondsToSelector:@selector(didFinishGettingArtist:)]) {
        NSLog(@"ArtistRepository calling didFinishFetchingArtist delegate");
        
        // generate entity from result
        
        Artist *artist        = [[Artist alloc] init];
		artist.mbid = [[result objectForKey:@"artist"] objectForKey:@"mbid"];
        artist.name = [[result objectForKey:@"artist"] objectForKey:@"name"];
        artist.url  = [[result objectForKey:@"artist"] objectForKey:@"url"];
        NSMutableArray *tags = [[NSMutableArray alloc] init];
        
NSLog(@"checkpoint 1");
        
        id tagsDict = [[result objectForKey:@"artist"] objectForKey:@"tags"];
        if ([tagsDict respondsToSelector:@selector(objectForKey:)] && [tagsDict objectForKey:@"tag"] != NULL) {
            for (NSDictionary *tag in [tagsDict objectForKey:@"tag"]) {
                [tags addObject:[tag objectForKey:@"name"]];
            }
        }
        artist.tags = tags;
NSLog(@"checkpoint 2");
        artist.biography = [[[result objectForKey:@"artist"] objectForKey:@"bio"] objectForKey:@"content"];
NSLog(@"checkpoint 3");
        for (NSDictionary *image in [[result objectForKey:@"artist"] objectForKey:@"image"]) {
            if ([[image objectForKey:@"size"] isEqualToString:@"large"] && ![[image objectForKey:@"#text"] isEqualToString:@""]) {
                artist.picture_url = [image objectForKey:@"#text"];
            }
        }
NSLog(@"checkpoint 3");
        // Echonest normalizing (unfinished, as we decided to go with Last.fm)
//        artist.name           = [[result objectForKey:@"artist"] objectForKey:@"name"];
//        artist.biography      = [[[result objectForKey:@"artist"] objectForKey:@"bio"] objectForKey:@"content"];
//        artist.external_links = [[result objectForKey:@"artist"] objectForKey:@"urls"];
        
        // handle external_links
//        for (NSDictionary *external_link in [[result objectForKey:@"artist"] objectForKey:@"external_links"]) {
//            NSString *catalog = [external_link objectForKey:@"catalog"];
//            NSString *link    = [[[external_link objectForKey:@"foreign_id"] componentsSeparatedByString:@":"] objectAtIndex:2];
//            
//            if ([catalog isEqualToString:@"twitter"]) {
//                [artist.external_links setValue:catalog 
//                                         forKey:[NSString stringWithFormat:@"http://www.twitter.com/%@",link]
//                 ];
//                
//                NSLog(@"Storing URL for %@ - %@", catalog, link);
//                
//            }
//        }
//        
//        NSDictionary *urls = [[result objectForKey:@"artist"] objectForKey:@"urls"];
//
//        for (NSString *key in urls) {
//            NSString *link = [urls objectForKey:key];
//            
//            [artist.external_links setValue:[[key componentsSeparatedByString:@"_"] objectAtIndex:0] forKey:link];
//            
//            NSLog(@"Storing URL for %@ - %@",[[key componentsSeparatedByString:@"_"] objectAtIndex:0], link);
//        }
        
//        NSArray *images = [[result objectForKey:@"artist"] objectForKey:@"images"];
//        
//        if ([images count] > 0) {
//            artist.picture_url = [[images objectAtIndex:0] objectForKey:@"url"];
//        }
        
        [self.delegate didFinishGettingArtist:artist];
        
        
        [artist release];
    }
}

- (void)errorGettingArtist:(NSError *)error
{
    NSLog(@"ArtistRepository errorFetchingArtist");
    if ([self.delegate respondsToSelector:@selector(errorGettingArtist:)]) {
        [self.delegate errorGettingArtist:error];
    }
}

@end

