module PocketQuest exposing (main)

import Dict exposing (Dict)
import Html exposing (Html)
import Html.Attributes as Attributes
import Inventory
import Material
import Material.Layout as Layout
import Material.Options as Options
import Material.Elevation as Elevation
import Material.Typography as Typo
import Overlay


type alias Model =
    { overlay : Overlay.Model
    , mdl : Material.Model
    }


type Msg
    = OverlayMsg Overlay.Msg
    | Mdl (Material.Msg Msg)


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
    let
        ( overlay, overlayCmd ) =
            Overlay.init
                [ { header = "Test"
                  , content = Html.text "Test"
                  }
                ]
    in
        { overlay = overlay
        , mdl = Material.model
        }
            ! [ Cmd.map (\msg -> OverlayMsg msg) overlayCmd ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        OverlayMsg msg_ ->
            let
                ( overlay, overlayCmd ) =
                    Overlay.update msg_ model.overlay
            in
                { model | overlay = overlay }
                    ! [ Cmd.map (\msg -> OverlayMsg msg) overlayCmd ]

        Mdl msg_ ->
            Material.update Mdl msg_ model


view : Model -> Html Msg
view model =
    Overlay.view model.overlay
        |> Html.map (\msg -> OverlayMsg msg)


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none
