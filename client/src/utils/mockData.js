export const maps = [
    {
        "mindmapId": 1,
        "title": "Map One",
        "creationDate": '01/04/2021',
        "editionDate": '03/09/2021',
        "lastEditorId": null,
        "creatorId": null,
        "collaborations": [
            {
                "collaboratorId": 1,
                "collaboratorEmail": "mail@mail.cz",
                "mindmapId": 2,
                "role": "EDITOR"
            },
            {
                "collaboratorId": 2,
                "collaboratorEmail": "test@mai.cz",
                "mindmapId": 2,
                "role": "READER"
            },
            {
                "collaboratorId": 11,
                "collaboratorEmail": "mailmail@mail.cz",
                "mindmapId": 2,
                "role": "EDITOR"
            },
            {
                "collaboratorId": 111,
                "collaboratorEmail": "mail1@mail.cz",
                "mindmapId": 2,
                "role": "EDITOR"
            },
            // {
            //     "collaboratorId": 1111,
            //     "collaboratorEmail": "mail@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
            // {
            //     "collaboratorId": 211,
            //     "collaboratorEmail": "test@mai.cz",
            //     "mindmapId": 2,
            //     "role": "READER"
            // },
            // {
            //     "collaboratorId": 11111,
            //     "collaboratorEmail": "mailmail@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
            // {
            //     "collaboratorId": 12,
            //     "collaboratorEmail": "mail1@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
            // {
            //     "collaboratorId": 122,
            //     "collaboratorEmail": "mail@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
            // {
            //     "collaboratorId": 2222,
            //     "collaboratorEmail": "test@mai.cz",
            //     "mindmapId": 2,
            //     "role": "READER"
            // },
            // {
            //     "collaboratorId": 13,
            //     "collaboratorEmail": "mailmail@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
            // {
            //     "collaboratorId": 1333,
            //     "collaboratorEmail": "mail1@mail.cz",
            //     "mindmapId": 2,
            //     "role": "EDITOR"
            // },
        ],
        "public": true
    },
    {
        "mindmapId": 2,
        "title": "test_vkladani",
        "creationDate": null,
        "editionDate": null,
        "lastEditorId": null,
        "creatorId": null,
        "collaborations": [
            {
                "collaboratorId": 1,
                "collaboratorEmail": "mail@mail.cz",
                "mindmapId": 2,
                "role": "EDITOR"
            },
            {
                "collaboratorId": 2,
                "collaboratorEmail": "test@mai.cz",
                "mindmapId": 2,
                "role": "READER"
            }
        ],
        "public": false
    }
];

export const roles = ['READER', 'EDITOR'];

export const users = [
    {
        "id": 1,
        "first_name": "Elina",
        "last_name": "Smirnova",
        "email": "mail@mail.cz",
        "mindmapId": 2,
        "role": "EDITOR"
    },
    {
        "id": 2,
        "first_name": "Lera",
        "last_name": "Chizh",
        "email": "lala@mail.cz",
        "mindmapId": 2,
        "role": "READER"
    },
    {
        "id": 3,
        "first_name": "Admin",
        "last_name": "Admin",
        "email": "mail@mail.cz",
        "mindmapId": 2,
        "role": "ADMIN"
    },
];

export const userMaps = {
    "yourMindmaps": [
        {
            "mindmapId": 1,
            "title": "test_mindmap",
            "creationDate": "2021-11-27T19:18:25",
            "editionDate": null,
            "lastEditorId": null,
            "creatorId": 8,
            "collaborations": [
                {
                    "collaboratorId": 1,
                    "collaborationId": 1,
                    "collaboratorEmail": "elina@test.cz",
                    "mindmapId": 1,
                    "role": "READER"
                },
                {
                    "collaboratorId": 10,
                    "collaborationId": 4,
                    "collaboratorEmail": "novy_kolaborator@test.cz",
                    "mindmapId": 1,
                    "role": "EDITOR"
                }
            ],
            "public": true
        }
    ],
    "sharedMindmaps": [
        {
            "mindmapId": 2,
            "title": "mindmap 2",
            "creationDate": "2021-12-06T00:00:00",
            "editionDate": null,
            "lastEditorId": null,
            "creatorId": 1,
            "collaborations": [
                {
                    "collaboratorId": 8,
                    "collaborationId": 2,
                    "collaboratorEmail": "test@test.cz",
                    "mindmapId": 2,
                    "role": "EDITOR"
                }
            ],
            "public": false
        },
        {
            "mindmapId": 8,
            "title": "mindmap 2",
            "creationDate": "2021-12-06T00:00:00",
            "editionDate": null,
            "lastEditorId": null,
            "creatorId": 1,
            "collaborations": [
                {
                    "collaboratorId": 8,
                    "collaborationId": 2,
                    "collaboratorEmail": "test@test.cz",
                    "mindmapId": 2,
                    "role": "EDITOR"
                }
            ],
            "public": true
        }
    ]
}