module Overlay exposing (Model, Msg, init, update, view, subscriptions)

import Dict exposing (Dict)
import Html exposing (Html)
import Material
import Material.Tabs as Tabs
import Material.Color as Color
import Material.Options as Options
import Material.Elevation as Elevation


type alias Model =
    { tabIndex : Int
    , tabs : Dict Int Tab
    , mdl : Material.Model
    }


type alias Tab =
    { header : String
    , content : Html Msg
    }


type Msg
    = SelectTab Int
    | Mdl (Material.Msg Msg)


init : List Tab -> ( Model, Cmd Msg )
init tabs =
    { tabIndex = 0
    , tabs =
        tabs
            |> List.indexedMap (,)
            |> Dict.fromList
    , mdl = Material.model
    }
        ! []


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SelectTab index ->
            { model | tabIndex = index } ! []

        Mdl msg_ ->
            Material.update Mdl msg_ model


view : Model -> Html Msg
view model =
    if Dict.size model.tabs > 0 then
        viewOverlay model
    else
        Html.text ""


viewOverlay : Model -> Html Msg
viewOverlay model =
    let
        tabProperties =
            [ Options.cs "overlay"
            , Tabs.onSelectTab SelectTab
            , Tabs.activeTab model.tabIndex
            ]

        tabLabels =
            Dict.values model.tabs
                |> List.map (\tab -> tab.header)
                |> List.map viewTabLabel

        tabContent =
            case Dict.get model.tabIndex model.tabs of
                Just tab ->
                    [ tab.content ]

                Nothing ->
                    []
    in
        Tabs.render Mdl
            [ 0 ]
            model.mdl
            tabProperties
            tabLabels
            tabContent


viewTabLabel : String -> Tabs.Label Msg
viewTabLabel header =
    Tabs.label
        [ Options.cs "overlay-tab"
        , Options.center
        , Color.background Color.primary
        , Color.text Color.white
        ]
        [ Html.text header ]


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none
