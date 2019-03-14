# Liferay Asset Dashboard

Currently Liferay Administrator has to visit asset specific pages to gather the statistics about individual Assets. For example, to see view count and average rating for any document and media file or web content admin has to open individual document. 

With the help of Dashboard, Administrator can view all the Assets on single page along with all the relative attributes. Portlet provides statistics about Web Content, Blogs, Wiki, Document and Media, Forms and Users.

Asset Dashboard component has below capabilities.
*	Configurable assets and date format
*	View asset wise total count
*	Sorting and searching feature
*	View asset status wise total count  
*	View asset different attributes like rating, like, dislike, status, version, total view, etc

## Environment

* Liferay 7.0 + DXP, Liferay 7.0 CE-GA5 +, Liferay 7.1 CE-GA1 +
* MySQL 5.6 +
* Make sure to uncheck Expose global property in JavaScript Loader, (Control Panel -> Configuration -> System Setting -> Foundation -> JavaScript Loader -> Expose global)

## How to use

1. Download, build and install asset dashboard jar on your server.
2. Check module status in liferay tomcat server using console log OR using gogo shell.
3. Now add Asset Dashboard portlet on specific page.


![ScreenShot](https://user-images.githubusercontent.com/24852574/39107818-60ff4bdc-46e2-11e8-8c16-10664c9bafd5.png)


Default view should look like below screenshot.


![ScreenShot](https://user-images.githubusercontent.com/24852574/39044365-8db5daba-44ad-11e8-9f13-a5aadca1be6f.png)

4.  Configure Assets and date format in portlet configuration like below screenshort.


![ScreenShot](https://user-images.githubusercontent.com/24852574/39044658-76db1818-44ae-11e8-969f-e426fc160be9.png)

![ScreenShot](https://user-images.githubusercontent.com/24852574/39044731-a92248dc-44ae-11e8-9c22-5c6146012156.png)

    
5. Display all configured assets and it's total entries.


![ScreenShot](https://user-images.githubusercontent.com/24852574/39044984-4f2d9592-44af-11e8-9ce3-24aeee81200b.png)


6. To see individual Assets record then click asset specific button. Below are the screenshots for individual Assets.


#### Form's ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045452-9976ff3e-44b0-11e8-920b-90aa694e7861.png)


#### Document & Media ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045598-ecf44cde-44b0-11e8-8f00-4a8315591f46.png)


#### Web Content ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045636-0af297ae-44b1-11e8-9309-cf7c5b73ee7a.png)


#### Wiki ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045677-1fef2528-44b1-11e8-9fb4-fe64e4f04c91.png)


#### User's ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045728-516602b6-44b1-11e8-9b4b-51135f0386e2.png)


#### Blog ####
![ScreenShot](https://user-images.githubusercontent.com/24852574/39045757-7471dbae-44b1-11e8-846a-5cdb999b12ca.png)






## Support
   Please feel free to contact us on hello@byteparity.com for any issue/suggestions.
