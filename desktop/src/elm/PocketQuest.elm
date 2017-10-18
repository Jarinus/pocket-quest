module PocketQuest exposing (main)

import Html exposing (Html)
import Html.Attributes as Attributes
import Material
import Material.Layout as Layout
import Material.Options as Options
import Material.Elevation as Elevation
import Material.Typography as Typo


type alias Model =
    { mdl : Material.Model }


type alias OverlayOptions =
    { id : String
    , header : String
    , content : List (Html Msg)
    }


type Msg
    = Mdl (Material.Msg Msg)


main : Program Never Model Msg
main =
    Html.program
        { init = init
        , subscriptions = subscriptions
        , update = update
        , view = view
        }


init : ( Model, Cmd Msg )
init =
    { mdl = Material.model } ! []


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Mdl msg_ ->
            Material.update Mdl msg_ model


view : Model -> Html Msg
view model =
    viewInventoryOverlay model


viewInventoryOverlay : Model -> Html Msg
viewInventoryOverlay model =
    viewOverlay model
        { id = "inventory-overlay"
        , header = "Inventory"
        , content = (viewInventoryOverlayContent model)
        }


viewInventoryOverlayContent : Model -> List (Html Msg)
viewInventoryOverlayContent model =
    []


viewOverlay : Model -> OverlayOptions -> Html Msg
viewOverlay model { id, header, content } =
    Options.div
        [ Options.id id
        , Elevation.e4
        ]
        [ Options.div
            [ Options.cs "overlay" ]
            [ Layout.render Mdl
                model.mdl
                [ Layout.fixedHeader
                , Layout.waterfall False
                ]
                { header =
                    [ Options.styled Html.h5
                        [ Typo.uppercase ]
                        [ Html.text header ]
                    ]
                , drawer = []
                , tabs = ( [], [] )
                , main = content
                }
            ]
        ]


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none
