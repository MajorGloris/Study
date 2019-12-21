# Kanji Study Application 


This application was designed with several ideologies in mind to hammer Japanese Kanji Characters efficiently and effectively in to the mind. These ideologies are as follows:

  - Spaced Repetition between study and learning sessions
  - Mnemonics
  - Reading and Writing for the sake of(researched) memorizing

# What is going on behind the scenes, programming wise:
    
   Please navigate Study/App/src/main/java/azynias/study for the entire project structure. 
    
   All information is stored locally in a SQLite database on the user's android device. The reason behind this was of course the lack of funding to have actual servers host a SQL database on my end. This does include some bad security limitations. The order, characters, and design of the database is the value of the application itself. Any hacker would immediately realize that the Android device is theirs and so is any data on the device. They would be able to extract the database at hand and 'steal' all data at hand and I would lose all value of this project. One must also note that legally this would also likely favor the owner of the advice, but is perhaps irrelevant as this project is a mere learning exercise. 
    
   There was use of Google's paying API with this application, as there was content available for purchase(more study material). This has potentially no security flaws as it is relying completely on Google's end of things - I did not design any of it. 
    
   Material Design principles were maintained in the design of this application. Android follows Material Design principles, so it would only make sense to follow them. 
    
   Singleton DBAccesser princple was followed for accessing the database. 
    
   AI OCR was depended on for comparing user drawn kanji characters to the existing database of actual characters. This relied on the Teseract engine as it was free to use. This also presented the same issue as the database, as it existed on the device itself though. As a result, the application also becomes bloated in size. 
    
   Android good practices were maintained(of the time) in development of this application. This includes using Activities for the sake of hosting fragments, of which fragments were responsible for logic of specific components to the application. 
    

# How does the user learn?

  Piotr Wozniak in the 90s had come to develop empirical findings on how he could hammer study content in his brain for as long as possible. He would record dates of study, review, and potentially when he forgot information. He had realized that there existed some mathematical formula with the potential of maximizing memory retention while minimizing study time. This is the SpacedMemo algorithm that is now used very heavily in popular applications such as Anki or WaniKani. 
  
  https://www.supermemo.com/en/archives1990-2015/english/ol/sm2
  
  The algorithm used in this project would be the SM2 edition, the most simple and bang for your buck version. To summarize, it is the consistency that existed in Peter's anecdotal, extensive, data. I refer anyone who is interested in the algorithm itself to look through Peter's portal themselves. 
  
  The Kanji application would apply exactly Peter's findings in to the philosophy of the program. A user would learn a kanji character through the learn section, and then review it X days later(according to the sm2 algorithm). The user would be notified of any material to review the moment they enter the application. 
  
  Study sessions would consist of the user writing a mnemonic for themself in order to memorize the kanji character properly. This would then be saved to the database and exists for potential updates incase the previos mnemonic did not serve them well. 
  
  Review sessions would also include a grade that would determine whether or not the user would be allowed to proceed to the next level of their study - A higher tier, which would of course include more kanji characters for them to study. 
  
  



